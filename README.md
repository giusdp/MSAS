# MSAS
Twitter (ex Mastodon) Sentiment Analysis in Scala 

Project for the Scalable and Cloud Programming course of the Master degree in Computer Science @ university of Bologna.

This application was developed in scala + spark to perform sentiment analysis on a stream of tweets (to get their positive/neutral/negative sentiment).

It was deployed on AWS Clusters, to do so:
- Build the jar with scala 2.11.12 and jdk 8;
- Upload it on a S3 bucket;
- Create an EMR cluster;
- Copy the jar on it (after accessing the cluster through ssh);
- Comment out the rows of /etc/spark/conf/log4j.properties that include DRFA stderr and stdout;
- Create a directory called Charts where the jar is;
- Launch the application with: "spark-submit --class main.Main namejar.jar **hashtag** **time**"

The **hashtag** argument is an hashtag of Twitter (the # symbol is not needed) used to filter the tweets.

The **time** argument is used to define the duration of the streaming. It must be of the following format: #{s | m | h}. 
It's quantity of time followed by s for seconds, m for minutes or h for hours. 

For example 30s defines 30 seconds of duration. 1h is 1 hour.

Example run: *spark-submit --class main.Main namejar.jar Trump 1m*

This launches a run on #Trump for 1 minute. Tweets will be downloaded and analysed and represented in a histogram after 1 minute.
