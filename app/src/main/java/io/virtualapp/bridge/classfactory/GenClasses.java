package io.virtualapp.bridge.classfactory;

import io.virtualapp.bridge.XC_MethodHook;
import java.util.ArrayList;
import org.ow2.asmdex.ApplicationWriter;
import org.ow2.asmdex.ClassVisitor;
import org.ow2.asmdex.MethodVisitor;
import org.ow2.asmdex.Opcodes;
import org.ow2.asmdex.structureCommon.Label;


/**
 * Created by bmax on 2018/4/3.
 */

public class GenClasses {

    public static void genClass(ApplicationWriter applicationWriter, GenedClassInfo genedClassInfo) {
        String genClassDesc = genedClassInfo.getClassDesc();
        ClassVisitor cv = applicationWriter.visitClass(Opcodes.ACC_PUBLIC, genClassDesc, null, FinalStr.objectDesc,
                null);
        genField(cv);
        genConstructor(cv, genClassDesc);
        genReplaceMethod(cv, genedClassInfo);
        genBackupMethod(cv, genedClassInfo);

        cv.visitEnd();
    }

    private static void genField(ClassVisitor cv) {
        cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, GenedClassInfo.paramsFieldName,
                XC_MethodHook.MethodHookParams.desc, null, null).visitEnd();
        cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, GenedClassInfo.callBackFieldName, XC_MethodHook.desc, null,
                null).visitEnd();
    }

    private static void genConstructor(ClassVisitor cv, String genClassDesc) {
        // init
        MethodVisitor mvInit = cv.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_CONSTRUCTOR, FinalStr.initFuncName,
                FinalStr.voidName + FinalStr.memberDesc + XC_MethodHook.desc, null, null);
        mvInit.visitCode();
        mvInit.visitMaxs(4, 0);
        // super();
        mvInit.visitMethodInsn(Opcodes.INSN_INVOKE_DIRECT, FinalStr.objectDesc, FinalStr.initFuncName,
                FinalStr.voidName, new int[]{1});

        // BudCallBack0.param = new MethodHookParams();
        mvInit.visitTypeInsn(Opcodes.INSN_NEW_INSTANCE, 0, 0, 0, XC_MethodHook.MethodHookParams.desc);
        mvInit.visitMethodInsn(Opcodes.INSN_INVOKE_DIRECT, XC_MethodHook.MethodHookParams.desc, FinalStr.initFuncName,
                FinalStr.voidName, new int[]{0});
        mvInit.visitFieldInsn(Opcodes.INSN_SPUT_OBJECT, genClassDesc, GenedClassInfo.paramsFieldName,
                XC_MethodHook.MethodHookParams.desc, 0, 0);
        // BudCallBack0.param.method = arg2;
        mvInit.visitFieldInsn(Opcodes.INSN_SGET_OBJECT, genClassDesc, GenedClassInfo.paramsFieldName,
                XC_MethodHook.MethodHookParams.desc, 0, 0);
        mvInit.visitFieldInsn(Opcodes.INSN_IPUT_OBJECT, XC_MethodHook.MethodHookParams.desc,
                XC_MethodHook.MethodHookParams.methodFieldName, FinalStr.memberDesc, 2, 0);
        // BudCallBack0.callBack = arg3;
        mvInit.visitFieldInsn(Opcodes.INSN_SPUT_OBJECT, genClassDesc, GenedClassInfo.callBackFieldName,
                XC_MethodHook.desc, 3, 0);

        mvInit.visitInsn(Opcodes.INSN_RETURN_VOID);

        mvInit.visitEnd();
    }

    private static void genReplaceMethod(ClassVisitor cv, final GenedClassInfo genedClassInfo) {

        final HookedMethodInfo hookedMethodInfo = genedClassInfo.getHookedMethodInfo();
        final int paramNo = hookedMethodInfo.getParamNo();
        final int paramRegNo = hookedMethodInfo.getParamRegNo();

        String genMethodDesc = genedClassInfo.getMethodDesc();

        // replace
        MethodVisitor mvReplace = null;
        if (hookedMethodInfo.hasThrowable()) {
            mvReplace = cv.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, GenedClassInfo.replaceMethodName,
                    genMethodDesc, null, new String[]{FinalStr.throwableDesc});
        } else {
            mvReplace = cv.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, GenedClassInfo.replaceMethodName,
                    genMethodDesc, null, null);
        }

        mvReplace.visitCode();
        final int localRegNo = 5;
        mvReplace.visitMaxs(localRegNo + paramRegNo, 0);

        // GenClass.class 0
        // MethodHookParams params = getMethodParams(clazz);1
        // XC_MethodHook callBack = getCallBack(clazz);2
        mvReplace.visitTypeInsn(Opcodes.INSN_CONST_CLASS, 0, 0, 0, genedClassInfo.getClassDesc());
        mvReplace.visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, GenedClassHelper.desc, GenedClassHelper.getMethodHookParamsMethodName,
                XC_MethodHook.MethodHookParams.desc + FinalStr.classDesc, new int[]{0});
        mvReplace.visitIntInsn(Opcodes.INSN_MOVE_RESULT_OBJECT, 1);
        mvReplace.visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, GenedClassHelper.desc, GenedClassHelper.getXCallBackMethodName,
                XC_MethodHook.desc + FinalStr.classDesc, new int[]{0});
        mvReplace.visitIntInsn(Opcodes.INSN_MOVE_RESULT_OBJECT, 2);

        // params.args = new Object[]; 0
        if (hookedMethodInfo.isStatic()) {
            mvReplace.visitVarInsn(Opcodes.INSN_CONST_4, 0, paramNo);
        } else {
            mvReplace.visitVarInsn(Opcodes.INSN_CONST_4, 0, paramNo - 1);
        }
        mvReplace.visitTypeInsn(Opcodes.INSN_NEW_ARRAY, 0, 0, 0, FinalStr.bracket + FinalStr.objectDesc);
        mvReplace.visitFieldInsn(Opcodes.INSN_IPUT_OBJECT, XC_MethodHook.MethodHookParams.desc,
                XC_MethodHook.MethodHookParams.argsFieldName, FinalStr.bracket + FinalStr.objectDesc, 0, 1);

        // wrap and assigned to MethodHookParam
        boolean isStatic = hookedMethodInfo.isStatic();
        BasicType[] basicTypes = hookedMethodInfo.getParamBasicTypes();

        int paramIndex = 0, arrIndex = 0, regIndex = localRegNo;

        if (!isStatic) {
            mvReplace.visitFieldInsn(Opcodes.INSN_IPUT_OBJECT, XC_MethodHook.MethodHookParams.desc,
                    XC_MethodHook.MethodHookParams.thisObjectFieldName, FinalStr.objectDesc, regIndex, 1);
            paramIndex++;
            regIndex++;
        }
        for (; paramIndex < paramNo; paramIndex++, arrIndex++, regIndex++) { // arrIndex always start with 0
            mvReplace.visitVarInsn(Opcodes.INSN_CONST_16, 3, arrIndex);
            BasicType type = basicTypes[paramIndex];
            if (type != null) {
                if (type.isWide()) {
                    mvReplace.visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, type.getWrapperDesc(),
                            type.getWrapMethodName(), type.getWrapperDesc() + type.getTypeDesc(),
                            new int[]{regIndex, ++regIndex});
                } else {
                    mvReplace.visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, type.getWrapperDesc(),
                            type.getWrapMethodName(), type.getWrapperDesc() + type.getTypeDesc(),
                            new int[]{regIndex});
                }
                mvReplace.visitIntInsn(Opcodes.INSN_MOVE_RESULT_OBJECT, 4);
                mvReplace.visitArrayOperationInsn(Opcodes.INSN_APUT_OBJECT, 4, 0, 3);
            } else {
                mvReplace.visitArrayOperationInsn(Opcodes.INSN_APUT_OBJECT, regIndex, 0, 3);
            }
        }

        // call before call
        mvReplace.visitMethodInsn(Opcodes.INSN_INVOKE_VIRTUAL, XC_MethodHook.desc, XC_MethodHook.beforeCallMethodName,
                FinalStr.voidName + XC_MethodHook.MethodHookParams.desc, new int[]{2, 1});

        // change parameters ,prepare call backup
        paramIndex = 0;
        arrIndex = 0;
        regIndex = localRegNo;
        if (!isStatic) {
            mvReplace.visitFieldInsn(Opcodes.INSN_IGET_OBJECT, XC_MethodHook.MethodHookParams.desc,
                    XC_MethodHook.MethodHookParams.thisObjectFieldName, FinalStr.objectDesc, localRegNo, 1);
            paramIndex++;
            regIndex++;
        }

        for (; paramIndex < paramNo; paramIndex++, arrIndex++, regIndex++) {
            mvReplace.visitVarInsn(Opcodes.INSN_CONST_16, 3, arrIndex);
            BasicType type = basicTypes[paramIndex];
            if (type != null) {
                mvReplace.visitArrayOperationInsn(Opcodes.INSN_AGET_OBJECT, 4, 0, 3);
                mvReplace.visitTypeInsn(Opcodes.INSN_CHECK_CAST, 0, 4, 0, type.getWrapperDesc());
                mvReplace.visitMethodInsn(Opcodes.INSN_INVOKE_VIRTUAL, type.getWrapperDesc(),
                        type.getUnwrapMethodName(), type.getTypeDesc(), new int[]{4});
                if (type.isWide()) {
                    mvReplace.visitIntInsn(Opcodes.INSN_MOVE_RESULT_WIDE, regIndex);// { regIndex ,regIndex ++ }
                    ++regIndex;
                } else {
                    mvReplace.visitIntInsn(Opcodes.INSN_MOVE_RESULT, regIndex);
                }
            } else {
                mvReplace.visitArrayOperationInsn(Opcodes.INSN_AGET_OBJECT, regIndex, 0, 3);
            }
        }

        //early return support
        Label lb_earlyReturn = new Label();
        mvReplace.visitMethodInsn(Opcodes.INSN_INVOKE_VIRTUAL, XC_MethodHook.MethodHookParams.desc, XC_MethodHook.MethodHookParams.isEarlyReturnMethodName,
                BasicType.BOOLEAN.getTypeDesc(), new int[]{1});
        mvReplace.visitIntInsn(Opcodes.INSN_MOVE_RESULT, 3);
        mvReplace.visitJumpInsn(Opcodes.INSN_IF_NEZ, lb_earlyReturn, 3, 0);

        // call backup , if Throwable ,try-catch
        Label lb_tryStart = null;
        Label lb_tryEnd = null;
        Label lb_tryHandler = null;
        if (hookedMethodInfo.hasThrowable()) {
            lb_tryStart = new Label();
            lb_tryEnd = new Label();
            lb_tryHandler = new Label();
            mvReplace.visitLabel(lb_tryStart);
        }

        // call backup
        int[] backupArgs = new int[paramRegNo];
        for (int i = 0; i < paramRegNo; i++) {
            backupArgs[i] = localRegNo + i;
        }

        if (paramRegNo <= 5) {
            mvReplace.visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, genedClassInfo.getClassDesc(),
                    GenedClassInfo.backupMethodName, genMethodDesc, backupArgs);
        } else {
            mvReplace.visitMethodInsn(Opcodes.INSN_INVOKE_STATIC_RANGE, genedClassInfo.getClassDesc(),
                    GenedClassInfo.backupMethodName, genMethodDesc, backupArgs);
        }

        if (hookedMethodInfo.hasThrowable()) {
            mvReplace.visitLabel(lb_tryEnd);
        }

        // get return value
        if (hookedMethodInfo.hasReturn()) {
            BasicType retType = hookedMethodInfo.getRetBasicType();
            if (retType != null) {// return Basic Type
                if (retType.isWide()) {
                    mvReplace.visitIntInsn(Opcodes.INSN_MOVE_RESULT_WIDE, 3); // reg 3 4
                    mvReplace.visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, retType.getWrapperDesc(),
                            retType.getWrapMethodName(), retType.getWrapperDesc() + retType.getTypeDesc(),
                            new int[]{3, 4});

                } else {
                    mvReplace.visitIntInsn(Opcodes.INSN_MOVE_RESULT, 3); // reg 3
                    mvReplace.visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, retType.getWrapperDesc(),
                            retType.getWrapMethodName(), retType.getWrapperDesc() + retType.getTypeDesc(),
                            new int[]{3});
                    // basic type wrapper 3
                }
            }
            // Object that wrap basic type returned or Object returned 3
            mvReplace.visitIntInsn(Opcodes.INSN_MOVE_RESULT_OBJECT, 3);
            mvReplace.visitFieldInsn(Opcodes.INSN_IPUT_OBJECT, XC_MethodHook.MethodHookParams.desc,
                    XC_MethodHook.MethodHookParams.resultFieldName, FinalStr.objectDesc, 3, 1);
        }

        mvReplace.visitLabel(lb_earlyReturn);
        // call after call
        mvReplace.visitMethodInsn(Opcodes.INSN_INVOKE_VIRTUAL, XC_MethodHook.desc, XC_MethodHook.afterCallMethodName,
                FinalStr.voidName + XC_MethodHook.MethodHookParams.desc, new int[]{2, 1});

        //mvReplace.visitLabel(lb_earlyReturn);
        // change return value and then return
        if (hookedMethodInfo.hasReturn()) {
            mvReplace.visitFieldInsn(Opcodes.INSN_IGET_OBJECT, XC_MethodHook.MethodHookParams.desc,
                    XC_MethodHook.MethodHookParams.resultFieldName, FinalStr.objectDesc, 0, 1);

            BasicType retType = hookedMethodInfo.getRetBasicType();
            if (retType != null) {// return Basic Type
                // Object -> Basic Type Wrapper
                mvReplace.visitTypeInsn(Opcodes.INSN_CHECK_CAST, 0, 0, 0, retType.getWrapperDesc());
                mvReplace.visitMethodInsn(Opcodes.INSN_INVOKE_VIRTUAL, retType.getWrapperDesc(),
                        retType.getUnwrapMethodName(), retType.getTypeDesc(), new int[]{0});
                if (retType.isWide()) { //
                    mvReplace.visitIntInsn(Opcodes.INSN_MOVE_RESULT_WIDE, 3); // 3 4
                    mvReplace.visitIntInsn(Opcodes.INSN_RETURN_WIDE, 3);
                } else {
                    mvReplace.visitIntInsn(Opcodes.INSN_MOVE_RESULT, 3);
                    mvReplace.visitIntInsn(Opcodes.INSN_RETURN, 3);
                }
            } else { // return Object
                mvReplace.visitIntInsn(Opcodes.INSN_RETURN_OBJECT, 0);
            }
        } else {
            mvReplace.visitInsn(Opcodes.INSN_RETURN_VOID);
        }

        // handler
        if (hookedMethodInfo.hasThrowable()) {//
            mvReplace.visitLabel(lb_tryHandler);
            // catch(Throwable 0
            mvReplace.visitIntInsn(Opcodes.INSN_MOVE_EXCEPTION, 0);
            // v1.throwable = throwable;
            mvReplace.visitFieldInsn(Opcodes.INSN_IPUT_OBJECT, XC_MethodHook.MethodHookParams.desc,
                    XC_MethodHook.MethodHookParams.throwableFieldName, FinalStr.throwableDesc, 0, 1);
            // -------still call after call----------
            mvReplace.visitMethodInsn(Opcodes.INSN_INVOKE_VIRTUAL, XC_MethodHook.desc, XC_MethodHook.afterCallMethodName,
                    FinalStr.voidName + XC_MethodHook.MethodHookParams.desc, new int[]{2, 1});
            //throw changed Throwable
            mvReplace.visitFieldInsn(Opcodes.INSN_IGET_OBJECT, XC_MethodHook.MethodHookParams.desc,
                    XC_MethodHook.MethodHookParams.throwableFieldName, FinalStr.throwableDesc, 0, 1);
            mvReplace.visitIntInsn(Opcodes.INSN_THROW, 0);
            //mvReplace.visitJumpInsn(Opcodes.INSN_GOTO_16,lb_tryEnd,0,0);
            mvReplace.visitTryCatchBlock(lb_tryStart, lb_tryEnd, lb_tryHandler, FinalStr.throwableDesc);
        }
        mvReplace.visitEnd();
    }

    private static void genBackupMethod(ClassVisitor cv, GenedClassInfo genedClassInfo) {
        final String genMethodDesc = genedClassInfo.getMethodDesc();
        // gen backup
        MethodVisitor mvBackup = null;
        if (genedClassInfo.getHookedMethodInfo().hasThrowable()) {
            mvBackup = cv.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, GenedClassInfo.backupMethodName,
                    genMethodDesc, null, new String[]{FinalStr.throwableDesc});
        } else {
            mvBackup = cv.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, GenedClassInfo.backupMethodName,
                    genMethodDesc, null, null);
        }
        mvBackup.visitCode();
        mvBackup.visitMaxs(genedClassInfo.getHookedMethodInfo().getParamRegNo() + 1, 0);
        // avoid java.lang.VerifyError: Rejecting class BudHook.GenClass_0 because it failed compile-time verification
        if (genedClassInfo.getHookedMethodInfo().hasReturn()) {
            BasicType retBasicType = genedClassInfo.getHookedMethodInfo().getRetBasicType();
            if (retBasicType != null) {
                if (retBasicType.isWide()) {
                    mvBackup.visitVarInsn(Opcodes.INSN_CONST_WIDE_16, 0, 0);
                    mvBackup.visitIntInsn(Opcodes.INSN_RETURN_WIDE, 0);
                } else {
                    mvBackup.visitVarInsn(Opcodes.INSN_CONST_4, 0, 0);
                    mvBackup.visitIntInsn(Opcodes.INSN_RETURN, 0);
                }
            } else {
                mvBackup.visitVarInsn(Opcodes.INSN_CONST_4, 0, 0);
                mvBackup.visitIntInsn(Opcodes.INSN_RETURN_OBJECT, 0);
            }
        } else {
            mvBackup.visitInsn(Opcodes.INSN_RETURN_VOID);
        }

        mvBackup.visitEnd();
    }

    public static byte[] genOneClassDexBytes(GenedClassInfo genedClassInfo) {
        ApplicationWriter aw = new ApplicationWriter();
        GenClasses.genClass(aw, genedClassInfo);
        aw.visitEnd();
        return aw.toByteArray();
    }


    public static byte[] genManyClassesDexBytes(ArrayList<GenedClassInfo> genedClassInfos) {
        ApplicationWriter aw = new ApplicationWriter();
        for (GenedClassInfo genedClassInfo : genedClassInfos) {
            GenClasses.genClass(aw, genedClassInfo);
        }
        aw.visitEnd();
        return aw.toByteArray();
    }

}
