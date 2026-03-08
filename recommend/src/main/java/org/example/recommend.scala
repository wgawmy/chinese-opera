//package com.example.springboot.recomend
//
//import org.apache.spark.ml.recommendation.ALS
//import org.apache.spark.sql.SparkSession
//import org.apache.spark.sql.functions.{col, explode}
//
//object MySparkJob {
//  def runJob(): Unit = {
//    val spark = SparkSession.builder()
//      .appName("OfflineRecommendation")
//      .master("local[*]")
//      .config("spark.ui.enabled", "false")
//      .getOrCreate()
//
//    // 读取评分数据
//    val ratings = spark.read.option("header","true").csv("hdfs://localhost:9000/opera/ratings.csv")
//    val ratingsDF = ratings
//      .withColumn("user_id", col("user_id").cast("int"))
//      .withColumn("movie_id", col("movie_id").cast("int"))
//      .withColumn("rate", col("rate").cast("double"))
//
//    // 训练 ALS 模型
//    val als = new ALS()
//      .setUserCol("user_id")
//      .setItemCol("movie_id")
//      .setRatingCol("rate")
//      .setRank(10)
//      .setMaxIter(10)
//      .setRegParam(0.1)
//
//    val model = als.fit(ratingsDF)
//
//    // 得到每个用户的 Top 10 推荐
//    val recommendations = model.recommendForAllUsers(10)
//
//    // explode 数组，把每条推荐结果拆成单行
//    val exploded = recommendations
//      .withColumn("rec", explode(col("recommendations")))
//      .select(
//        col("user_id"),
//        col("rec.movie_id").alias("movie_id"),
//        col("rec.rating").alias("rating")
//      )
//
//    // 写入 MySQL
//    exploded.write
//      .format("jdbc")
//      .option("url", "jdbc:mysql://localhost:3308/chineseopera")
//      .option("dbtable", "user_recommend")
//      .option("user", "root")
//      .option("password", "mysql")
//      .mode("overwrite")
//      .save()
//  }
//}
//
//object Main {
//  def main(args: Array[String]): Unit = {
//    MySparkJob.runJob
//  }
//}
