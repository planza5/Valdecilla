package com.plm.valdecilla;

public interface IHandlerCallback {
    public void startTask(int task, Object... values);

    public void endTask(int task, Object... values);
}
