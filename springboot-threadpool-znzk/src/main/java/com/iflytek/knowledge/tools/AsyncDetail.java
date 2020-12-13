package com.iflytek.knowledge.tools;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.Future;

@Component
public class AsyncDetail {

    @Async("taskExecutor")
    public Future<String> syncMargePsr(List<String> bookList, int pageIndex){

        System.out.println("time="+System.currentTimeMillis()+",thread id="+Thread.currentThread().getName()+",pageIndex="+pageIndex);

        //声明future对象
        Future<String> result = new AsyncResult<String>("thread id="+Thread.currentThread().getName());
        //循环遍历该段旅客集合
        if(null != bookList && bookList.size() >0){
            for(String book: bookList){
                try {
                    //数据入库操作
                } catch (Exception e) {
                    //记录出现异常的时间，线程name
                    result = new AsyncResult<String>("fail,time="+System.currentTimeMillis()+",thread id="+Thread.currentThread().getName()+",pageIndex="+pageIndex);
                    continue;
                }
            }
        }
        return result;
    }
}
