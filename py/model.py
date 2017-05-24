# -*- coding:utf-8 -*-

import tensorflow as tf
import inventor_data


# TensorFlow r1.0
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'

# 获取数据
train_x, train_y, test_x, test_y = inventor_data.get_data(10)
n = len(train_x[0])

# 开始实现 TensorFlow 模型
def init_weights(shape, name):
    return tf.Variable(tf.random_normal(shape, stddev=0.01), name=name)

with tf.name_scope('input_layer') as scope:
    x = tf.placeholder(tf.float32, [None, n], name='input_x')

# hidden_1
with tf.name_scope('hidden_1') as scope:
    w_layer_1 = init_weights([n, 32], name='w_layer_1')
    b_layer_1 = tf.Variable(tf.zeros([32]), name='b_layer_1')
    hidden_1 = tf.nn.relu(tf.matmul(x, w_layer_1) + b_layer_1)
    tf.summary.histogram("w_layer_1", w_layer_1)

# hidden_2
with tf.name_scope('hidden_2') as scope:
    w_layer_2 = init_weights([32, 64], name='w_layer_2')
    b_layer_2 = tf.Variable(tf.zeros([64]), name='b_layer_2')
    hidden_2 = tf.nn.relu(tf.matmul(hidden_1, w_layer_2) + b_layer_2)
    tf.summary.histogram("w_layer_2", w_layer_2)

# hidden_3
with tf.name_scope('hidden_3') as scope:
    w_layer_3 = init_weights([64, 64], name='w_layer_3')
    b_layer_3 = tf.Variable(tf.zeros([64]), name='b_layer_3')
    hidden_3 = tf.nn.relu(tf.matmul(hidden_2, w_layer_3) + b_layer_3)
    tf.summary.histogram("w_layer_3", w_layer_3)

# dropout
keep_prob = tf.placeholder("float")
hidden_3_drop = tf.nn.dropout(hidden_3, keep_prob)

# output
with tf.name_scope('output_layer') as scope:
    W = init_weights([64, 3], name='w_layer_3')
    b = tf.Variable(tf.zeros([3]), name='b_layer_3')
    y = tf.nn.softmax(tf.matmul(hidden_3_drop, W) + b, name='predict_y')

# y 是我们预测的概率分布, y_ 是实际的分布
y_ = tf.placeholder("float", [None, 3], name='label_y')
with tf.name_scope('cost_function') as scope:
    cross_entropy = -tf.reduce_sum(y_*tf.log(y))
    tf.summary.scalar('cross_entropy', cross_entropy)

with tf.name_scope('train'):
    train_step = tf.train.GradientDescentOptimizer(0.006).minimize(cross_entropy)

# 评估我们的模型
correct_prediction = tf.equal(tf.argmax(y, 1), tf.argmax(y_, 1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, "float"), name='accuracy')
tf.summary.scalar('accuracy', accuracy)

# 启动会话
sess = tf.Session()
# sess = tf.InteractiveSession()
init = tf.global_variables_initializer()
sess.run(init)
# tf.global_variables_initializer().run()

# TensorBoard
merged = tf.summary.merge_all()
train_writer = tf.summary.FileWriter('../my_logs/train', sess.graph)
test_writer = tf.summary.FileWriter('../my_logs/test')

# 开始训练
for j in range(100):
    for i in range(20):
        batch_xs = train_x[i*50:i*50+50]
        batch_ys = train_y[i*50:i*50+50]
        summary, _ = sess.run([merged, train_step], feed_dict={x: batch_xs, y_: batch_ys, keep_prob: 1.0})
        train_writer.add_summary(summary, j*20 + i)
    train_accuracy = sess.run(accuracy, feed_dict={x: train_x, y_: train_y, keep_prob: 1.0})
    print("step %d, training accuracy %g" % (j*20, train_accuracy))

# 测试集准确度
summary, test_accuracy = sess.run([merged, accuracy], feed_dict={x: test_x, y_: test_y, keep_prob: 1.0})
print("test accuracy %g" % test_accuracy)

# 保存模型参数
# saver = tf.train.Saver()
# save_path = saver.save(sess, "/tmp/my_model.ckpt")
# print "Model saved in file: ", save_path

sess.close()

# 启动 TensorBoard
# tensorboard --logdir=./my_logs
