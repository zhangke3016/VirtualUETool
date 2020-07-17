package com.cheng.automate.core.helper

import com.cheng.automate.core.model.ElementBean
import com.cheng.automate.core.model.MMKVUtil

/**
 * @author zijian.cheng
 * @date 2020/7/17
 */
object FlowClickDataHelper {

    private var beforeElement: MutableList<ElementBean>? = null

    var index = 0

    var loop = true

    /**
     * 重置列表
     */
    fun resetElementList() {
        index = 0
        beforeElement = MMKVUtil.getInstance().getElements("elementBeans")
    }

    fun getCurrentElement(): ElementBean? {
        if (beforeElement == null) {
            resetElementList()
        }
        beforeElement?.let {
            if (index < it.size) {
                return it[index]
            } else if (loop) {
                index = 0
                getCurrentElement();
            }
        }
        return null
    }

    fun ascendIndex() {
        index++
    }
}