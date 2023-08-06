// ILibraryInterface.aidl
package com.example.aidldemo;

// 导入所需要使用的非默认支持数据类型对应的aidl文件的包，注意是aidl的包，不是实体数据的包
import com.example.aidldemo.Book;
import com.example.aidldemo.Author;
import com.example.aidldemo.IRemoteServiceCallback;

// 注意： 如果有非默认支持的数据类型需要被out修饰， 那实体类需要实现readFromParcel方法
interface ILibraryInterface {
    void addBooks(in List<Book> books);
    List<Book> getBooks();
    List<Author> getAuthors();
    void testInOut(in Book inBook, out Book outBook, inout Book inoutBook);
    oneway void testOnway();
    void registerCallback(IRemoteServiceCallback callback);
}