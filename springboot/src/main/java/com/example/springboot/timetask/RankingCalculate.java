//package com.example.springboot.timetask;
//
//import com.example.springboot.mapper.RankingMapper;
//import com.example.springboot.service.AccessService;
//import com.example.springboot.service.OperaService;
//import com.example.springboot.utils.RedisCache;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.InputStreamReader;
//
//@Component
//@Slf4j
//public class RankingCalculate implements ApplicationRunner {
//    @Autowired
//    OperaService operaService;
//
//    @Autowired
//    RankingMapper rankingMapper;
//
//    @Autowired
//    AccessService accessService;
//
//    @Autowired
//    RedisCache redisCache;
//
//    @Scheduled(cron="0 0 */12 * * ?")
//    @Transactional
//    public void calculateRankings() {
//        // ...排行榜更新逻辑
//    }
//
//    @Scheduled(cron = "0 0 3 * * ?")
//    public void generateRecommendations(){
//        runPythonScript();
//    }
//
//    // 启动项目后执行一次，如果想要快速演示
////    @Override
////    public void run(ApplicationArguments args) {
////        log.info("启动后立即执行推荐脚本");
////        System.out.println("开始执行啦！");
////        generateRecommendations(); // 或者直接 runPythonScript();
////    }
//    //TODO：推荐算法目前是使用json的形式，后续可以采用redis优化的方式
//    //TODO：python的接口已经写好了，可以从前端根据用户的行为进行点击
//    private void runPythonScript() {
//        log.info("开始执行推荐列表定时更新任务");
//        try {
//            ProcessBuilder pb = new ProcessBuilder("python", "run_ncf.py");
//            pb.directory(new File("D:\\code\\Python\\liyuan\\experiments"));
//            pb.redirectErrorStream(true);
//            Process process = pb.start();
//
//            // 可选：读取输出方便调试
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    log.info("Python输出: {}", line);
//                }
//            }
//
//            int exitCode = process.waitFor();
//            if (exitCode != 0) {
//                throw new RuntimeException("Python 脚本执行失败，exit code = " + exitCode);
//            }
//
//        } catch (Exception e) {
//            log.error("执行推荐脚本失败", e);
//        }
//        System.out.println("执行成功");
//    }
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//
//    }
//}
