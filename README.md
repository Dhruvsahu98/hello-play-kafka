# hello-play-kafka
Experiment Play with Kafka and Akka Streams

Ref: https://github.com/jamesward/hello-play-kafka

Given topic "RandomNumbers" exists with Kafka URL be like "localhost:9092", commands to test like below.

```
sbt ~run
```
Then navigate to `localhost:9000/{groupId}` to see how different consumers receive Kafka message. Note that I disabled Auto commit and set auto.commit.reset to be "earliest" to fetch all available messages.


```
sbt "runMain workers.RandomNumbers"
```
To generate random Kafka messages every 500 milliseconds.
