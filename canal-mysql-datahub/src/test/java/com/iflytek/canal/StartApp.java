package com.iflytek.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan(basePackages = "com.iflytek.canal")
public class StartApp implements CommandLineRunner {

    @Autowired
    public CanalUtil canalUtil;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(StartApp.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        CanalConnector connector = canalUtil.getCanalConnector();
        int batchSize = 1000;
        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();
            while (true) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                } else {
                    canalUtil.printEntry(message.getEntries());
                }
                connector.ack(batchId); // 提交确认
//                connector.rollback(batchId); // 处理失败, 回滚数据
            }
        } finally {
            connector.disconnect();
        }

    }
}
