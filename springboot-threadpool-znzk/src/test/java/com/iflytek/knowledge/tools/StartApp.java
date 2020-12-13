package com.iflytek.knowledge.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@EnableAutoConfiguration
@ComponentScan(basePackages = "com.iflytek.knowledge.tools")
public class StartApp implements CommandLineRunner {

    @Autowired
    private AsyncDetail asyncDetail;

    public List<String> getPsrList(){
        List<String> bookList = new ArrayList<String>();
        for(int i=0 ; i<2000 ;i++){
            bookList.add("ztwu4-"+i);
        }
        return bookList;
    }

    public void receiveBookJobRun(){
        List<String> bookList = null;
        bookList  = getPsrList();
        //入库开始时间
        Long inserOrUpdateBegin = System.currentTimeMillis();
        //接收集合各段的 执行的返回结果
        List<Future<String>> futureList = new ArrayList<Future<String>>();
        //集合总条数
        if(bookList != null){
            int listSize = bookList.size();
            int listStart,listEnd;
            //将list 切分多份 多线程执行
            for (int i = 0; i < 10; i++) {
                //计算切割  开始和结束
                listStart = listSize / 100 * i ;
                listEnd = listSize / 100 * ( i + 1 );
                //最后一段线程会 出现与其他线程不等的情况
                if(i == 10 - 1){
                    listEnd = listSize;
                }
                //数据切断
                List<String> sunList = bookList.subList(listStart,listEnd);
                //每段数据集合并行入库
                futureList.add(asyncDetail.syncMargePsr(sunList,i));

            }
            //对各个线程段结果进行解析
            for(Future<String> future : futureList){
                while (true){
                    String str ;
                    if(null != future && future.isDone() ){
                        try {
                            System.out.println("输出结果================");
                            str = future.get().toString();
                            System.out.println("data: "+str);
                            break;
                        } catch (InterruptedException | ExecutionException e) {
                        }

                    }else{

                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(StartApp.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        receiveBookJobRun();
        System.exit(0);
    }
}
