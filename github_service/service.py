import pulsar

client = pulsar.Client('pulsar://localhost:6650')
consumer = client.subscribe('github-service',
                            subscription_name='first-of-my-name')

while True:
    msg = consumer.receive()
    print("Received message: '%s'" % msg.data())
    consumer.acknowledge(msg)

client.close()
