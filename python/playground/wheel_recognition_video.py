import numpy as np
import cv2
import math
from line_recognition import findEdgesAndLines
from select_threshold_lines import getImageWithEdgedAndLines, filterByAngle, angleOfLine, angleToDeg
from wheel_recognition.wheelsRecognition import WheelsRecognition


def findLinesInROI(image, roiY, roiX, cannyThres1, cannyThres2, lineThres, minLineLength, maxLineGap, fromAngle, toAngle):
    roiImage = image[roiY[0]:roiY[1], roiX[0]:roiX[1]]
    (edge, lines) = findEdgesAndLines(roiImage, (cannyThres1,
                                                 cannyThres2), (lineThres, minLineLength, maxLineGap))

    lines = filterByAngle(lines, fromAngle, toAngle)
    pass
    return [[(line[0][0] + roiX[0], line[0][1] + roiY[0], line[0][2] + roiX[0], line[0][3] + roiY[0])] for line in lines]


cap = cv2.VideoCapture(
    'D:/Users/pawel/Google Drive/mindstorms/database/video/video.mp4')

recogintion = WheelsRecognition(
    (1275, 624), (34, 32, 21), ((390, 480), (0, 220)), ((390, 480), (440, 640)), (-math.pi/12, math.pi/3), (-math.pi/3, math.pi/12))

while(cap.isOpened()):
    ret, frame = cap.read()
    if ret == True:
        (linesLeftWheel, linesRightWheel) = recogintion.findWheelsLines(frame)

        outputImage = getImageWithEdgedAndLines(frame, None, linesLeftWheel + linesRightWheel)
    
        wheelInfoStr = recogintion.formatStatistic((linesLeftWheel, linesRightWheel))

        cv2.putText(outputImage, wheelInfoStr, (5, 20),
                    cv2.FONT_HERSHEY_SIMPLEX, .4, (255, 255, 255), 1, cv2.LINE_AA)
        print(wheelInfoStr)
        cv2.imshow('frame', outputImage)
    if cv2.waitKey(20) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
