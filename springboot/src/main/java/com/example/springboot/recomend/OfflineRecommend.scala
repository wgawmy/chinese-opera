//package com.example.springboot.recomend
//
//import org.apache.spark.sql.SparkSession
//import org.apache.spark.sql.functions.col
//import org.apache.spark.ml.recommendation.ALS
//object MySparkJob {
//  def runJob(): Unit = {
//    val spark = SparkSession.builder()
//      .appName("OfflineRecommendation")
//      .master("local[*]")
//      .config("spark.ui.enabled", "false")
//      .getOrCreate()
//
//    val ratings = spark.read.option("header","true").csv("hdfs://server:9000/opera/ratings.csv")
//    val ratingsDF = ratings
//      .withColumn("userId", col("userId").cast("int"))
//      .withColumn("itemId", col("itemId").cast("int"))
//      .withColumn("rating", col("rating").cast("double"))
//    val als = new ALS()
//      .setUserCol("userId")
//      .setItemCol("itemId")
//      .setRatingCol("rating")
//      .setRank(10)
//      .setMaxIter(10)
//      .setRegParam(0.1)
//
//    val model = als.fit(ratingsDF)
//    val recommendations = model.recommendForAllUsers(10)
////    recommendations.write
////      .format("jdbc")
////      .option("url", "jdbc:mysql://localhost:3306/recommend")
////      .option("dbtable", "user_recommend")
////      .option("user", "root")
////      .option("password", "123456")
////      .mode("overwrite")
////      .save()
//  }
//}
//
//object Main {
//  def main(args: Array[String]): Unit = {
//    MySparkJob.runJob
//  }
//}
