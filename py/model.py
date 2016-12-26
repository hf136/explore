# -*- coding:utf-8 -*-

import tensorflow as tf
import numpy as np

# 读取发明家发明专利的 csv 数据
import csv
data = []
with open('inventor_hist_dropna_data.csv') as f:
    f_csv = csv.reader(f)
    headers = next(f_csv)
    for row in f_csv:
        data.append(row)

inve = []
trend = []
for line in data:
    e = [float(x) for x in line[:7]]
    pre = sum(float(x) for x in line[4:7]) / 3.0
    now = sum(float(x) for x in line[7:]) / 3.0
    label = [0, 1, 0]
    if now - pre > 1:
        label = [1, 0, 0]
    elif now - pre < -1:
        label = [0, 0, 1]
    inve.append(e)
    trend.append(label)

# 切分数数据， 70% 为训练集， 30% 为测试集
split_index = int(len(inve)*0.7)
train_x = inve[:split_index]
train_y = trend[:split_index]
test_x = inve[split_index:]
test_y = trend[split_index:]

# 开始实现 TensorFlow 模型
def init_weights(shape, name):
    return tf.Variable(tf.random_normal(shape, stddev=0.01), name=name)

with tf.name_scope('input_layer') as scope:
    x = tf.placeholder(tf.float32, [None, 7], name='train_x')
# hidden_1
with tf.name_scope('hidden_1') as scope:
    w_layer_1 = init_weights([7, 512], name='w_layer_1')
    b_layer_1 = tf.Variable(tf.zeros([512]), name='b_layer_1')
    hidden_1 = tf.nn.relu(tf.matmul(x, w_layer_1) + b_layer_1)
# hidden_2
with tf.name_scope('hidden_2') as scope:
    w_layer_2 = init_weights([512, 512], name='w_layer_2')
    b_layer_2 = tf.Variable(tf.zeros([512]), name='b_layer_2')
    hidden_2 = tf.nn.relu(tf.matmul(hidden_1, w_layer_2) + b_layer_2)
# output
with tf.name_scope('output_layer') as scope:
    W = init_weights([512, 3], name='w_layer_3')
    b = tf.Variable(tf.zeros([3]), name='b_layer_3')
    y = tf.nn.softmax(tf.matmul(hidden_2, W) + b, name='predict_y')
# y 是我们预测的概率分布, y' 是实际的分布
y_ = tf.placeholder("float", [None, 3], name='train_y')
cross_entropy = -tf.reduce_sum(y_*tf.log(y))
train_step = tf.train.GradientDescentOptimizer(0.01).minimize(cross_entropy)
init = tf.initialize_all_variables()
sess = tf.Session()
sess.run(init)
# 评估我们的模型
correct_prediction = tf.equal(tf.argmax(y, 1), tf.argmax(y_, 1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, "float"), name='accuracy')
# 开始训练
for i in range(100):
    # batch_xs, batch_ys = mnist.train.next_batch(100)
    batch_xs = train_x[i*50:i*50+50]
    batch_ys = train_y[i*50:i*50+50]
    sess.run(train_step, feed_dict={x: batch_xs, y_: batch_ys})
    train_accuracy = (sess.run(accuracy, feed_dict={x: batch_xs, y_: batch_ys}))
    print "step %d, training accuracy %g" % (i, train_accuracy)

# 测试集准确度
print(sess.run(accuracy, feed_dict={x: test_x, y_: test_y}))

# TensorBoard
merged_summary_op = tf.merge_all_summaries()
summary_writer = tf.train.SummaryWriter('/tmp/mnist_logs', sess.graph)

# 保存模型参数
saver = tf.train.Saver()
save_path = saver.save(sess, "/tmp/my_model.ckpt")
print "Model saved in file: ", save_path

sess.close()
