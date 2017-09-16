package com.icourt;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import jdk.nashorn.internal.codegen.CompilerConstants;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/30
 * version 2.1.0
 */
public interface tetss {
     Queue<String> callQueue=new ConcurrentLinkedQueue<String>();
    final Stack<WeakReference<CompilerConstants.Call>> callStack = new Stack<>();//网络请求堆栈

}
