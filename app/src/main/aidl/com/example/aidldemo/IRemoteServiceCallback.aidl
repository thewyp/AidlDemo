// IRemoteServiceCallback.aidl
package com.example.aidldemo;


interface IRemoteServiceCallback {
    // 注意这里是使用定向tag in， 因为IRemoteServiceCallback是在客户端实现，
    // call这个方法时客户端变成了服务端
    void completed(int type, in Bundle bundle);
}