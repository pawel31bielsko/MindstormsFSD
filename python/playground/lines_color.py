import cv2
import numpy as np
import matplotlib
from matplotlib.pyplot import imshow
from matplotlib import pyplot as plt

# white color mask
img = cv2.imread('src.jpg')

cv2.imshow("src",img) 
#converted = convert_hls(img)
image = cv2.cvtColor(img,cv2.COLOR_BGR2HLS)
lower = np.uint8([0, 0, 0])
upper = np.uint8([179, 255, 98])
mask = cv2.inRange(image, lower, upper)


cv2.imshow("mask",mask) 
cv2.waitKey(0)
