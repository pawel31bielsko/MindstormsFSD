import cv2
import numpy as np
import math


def findEdgesAndLines(inputImage, cannyParams, houghLinesParams):
    gray = cv2.cvtColor(inputImage, cv2.COLOR_BGR2GRAY)
    # blur_gray = cv2.GaussianBlur(gray,(5, 5),0)
    edge = cv2.Canny(gray, cannyParams[0], cannyParams[1], apertureSize=5)
    lines = cv2.HoughLinesP(
        edge, 1, math.pi/180.0, houghLinesParams[0], np.array([]), houghLinesParams[1], houghLinesParams[2])
    if lines is None:
        return (edge, [])
    else:
        return (edge, lines)
