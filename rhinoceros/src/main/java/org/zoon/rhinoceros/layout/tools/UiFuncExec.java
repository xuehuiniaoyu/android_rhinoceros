package org.zoon.rhinoceros.layout.tools;

import org.zoon.rhinoceros.utils.Reflect;

/**
 *
 * 某些方法不能在解析的时候直接调用，必须要等到UI加载完才能计算
 * 所以必须要等到被执行了onMeasure方法后才会被调用
 * 由此方法必须被保存起来
 */
public abstract class UiFuncExec {
    String name;
    Object[] values;
    public UiFuncExec(String name, Object... values) {
        this.name = name;
        this.values = values;
    }
    protected abstract void onExec(Object[] values);

    public UiFuncExec exec() {
        onExec(this.values);
        return this;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(obj instanceof UiFuncExec) {
            if (((UiFuncExec) obj).name.equals(this.name))
                return true;
        }
        return false;
    }
}
