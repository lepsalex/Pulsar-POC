import os
import pulsar

print("Github Service start!")

url = os.getenv('SERVICE_URL', 'pulsar://localhost:6650')
client = pulsar.Client(url)
consumer = client.subscribe("task-new", "github-service")

print("Github Service running ...")

while True:
    try:
        print("Listening on topics: {}".format(consumer.topic()))
        msg = consumer.receive()
        print("Received message '{}' id='{}'".format(msg.data(), msg.message_id()))
        # Acknowledge successful processing of the message
        consumer.acknowledge(msg)
    except:
        # Message failed to be processed
        print("Message failed to be processed!")
        consumer.negative_acknowledge(msg)

client.close()
