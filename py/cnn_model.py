# -*- coding:utf-8 -*-

import tensorflow as tf


sess = tf.InteractiveSession()

# 获取数据
n = [7, 1000]

# 开始实现 TensorFlow 模型
def weight_variable(shape, name):
    initial = tf.truncated_normal(shape, stddev=0.1, name=name)
    return tf.Variable(initial)

def bias_variable(shape, name):
    initial = tf.constant(0.1, shape=shape, name=name)
    return tf.Variable(initial)

def conv2d(x, W):
    return tf.nn.conv2d(x, W, strides=[1, 1, 1, 1], padding='SAME')

def max_pool_2x2(x):
    return tf.nn.max_pool(x, ksize=[1, 2, 2, 1],
                          strides=[1, 2, 2, 1], padding='SAME')

with tf.name_scope('input_layer_1') as scope:
    x = tf.placeholder(tf.float32, [None, 7*1000], name='input_x1')
    x_mat = tf.reshape(x, [-1, 7, 1000, 1])

# 卷积层1
with tf.name_scope('conv_layer_1') as scope:
    W_conv1 = weight_variable([5, 5, 1, 3], "W_conv1")
    b_conv1 = bias_variable([3], "b_conv1")
    h_conv1 = tf.nn.relu(conv2d(x_mat, W_conv1) + b_conv1)

with tf.name_scope('max_pooling_1') as scope:
    h_pool1 = max_pool_2x2(h_conv1)

# 卷积层2
with tf.name_scope('conv_layer_2') as scope:
    W_conv2 = weight_variable([3, 10, 3, 3], "W_conv2")
    b_conv2 = bias_variable([3], "b_conv2")
    h_conv2 = tf.nn.relu(conv2d(h_pool1, W_conv2) + b_conv2)

with tf.name_scope('max_pooling_2') as scope:
    h_pool2 = max_pool_2x2(h_conv2)

# 全连接层1
with tf.name_scope('full_connect_1') as scope:
    W_fc1 = weight_variable([2 * 250 * 3, 20], name="W_fc1")
    b_fc1 = bias_variable([20], name="b_fc1")
    h_pool2_flat = tf.reshape(h_pool2, [-1, 2 * 250 * 3])
    h_fc1 = tf.nn.relu(tf.matmul(h_pool2_flat, W_fc1) + b_fc1)

with tf.name_scope('input_layer_2') as scope:
    x_vec = tf.placeholder(tf.float32, [None, 10], name='input_x2')

with tf.name_scope("concat") as scope:
    x_concat = tf.concat([h_fc1, x_vec], 1)

# 全连接层2
with tf.name_scope('full_connect_2') as scope:
    W_fc2 = weight_variable([30, 32], name="W_fc2")
    b_fc2 = bias_variable([32], name="b_fc2")
    h_fc2 = tf.nn.relu(tf.matmul(x_concat, W_fc2) + b_fc2)

# dropout
keep_prob = tf.placeholder(tf.float32)
h_fc2_drop = tf.nn.dropout(h_fc2, keep_prob)

# output
with tf.name_scope('output_layer') as scope:
    W = weight_variable([32, 3], name='W')
    b = tf.Variable(tf.zeros([3]), name='b')
    y = tf.nn.softmax(tf.matmul(h_fc2_drop, W) + b, name='predict_y')

# y 是我们预测的概率分布, y' 是实际的分布
y_ = tf.placeholder(tf.float32, [None, 3], name='label_y')

with tf.name_scope("cost_function") as scope:
    cross_entropy = -tf.reduce_sum(y_*tf.log(y))

# 评估我们的模型
with tf.name_scope("evaluation") as scope:
    correct_prediction = tf.equal(tf.argmax(y, 1), tf.argmax(y_, 1))
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, "float"), name='accuracy')

with tf.name_scope('train'):
    train_step = tf.train.GradientDescentOptimizer(0.003).minimize(cross_entropy)

train_writer = tf.summary.FileWriter('./my_logs/cnn_model/train', sess.graph)
