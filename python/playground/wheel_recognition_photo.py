import cv2
import numpy as np
import matplotlib
from matplotlib.pyplot import imshow
from matplotlib import pyplot as plt
from select_threshold_lines import selectLineDetectionThresholds

import os

path = 'D:/Users/pawel/Google Drive/mindstorms/database/photos/fix_camera/morning_wheel_trun'

filePaths = [os.path.join(path, file)
             for file in os.listdir(path) if file.endswith('.jpg')]
images = [cv2.imread(filePath) for filePath in filePaths[:20]]
rightWheels = [img[360:480, 450:640] for img in images]
selectLineDetectionThresholds(rightWheels, 'edge', 1275, 624, 34, 32, 21)


# img = cv2.imread(
#     'D:/Users/pawel/Google Drive/mindstorms/database/photos/fix_camera/morning_wheel_trun')

# wheel_left = img[360:480, 0:190]
# wheel_right = img[360:480, 450:640]
