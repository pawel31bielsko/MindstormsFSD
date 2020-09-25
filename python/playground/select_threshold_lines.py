import cv2
import numpy as np
import math
from line_recognition import findEdgesAndLines


def nothing(x):
    pass


def angleOfLine(line):
    return math.atan((line[0][3] - line[0][1]) /
                     (line[0][2] - line[0][0]))


def angleToDeg(angle):
    return angle / (2 * math.pi) * 360


def getImageWithEdgedAndLines(inputImage, edge, lines):
    vis = inputImage.copy()
    vis = np.uint8(vis/2.)
    if(edge != None):
        vis[edge != 0] = (0, 255, 0)   

    for line in lines:
        cv2.line(vis, (line[0][0], line[0][1]), (line[0][2],
                                                 line[0][3]), (0, 0, 255), 3, cv2.LINE_AA)
    return vis


def concatImages(inputImages, columnSize):
    blankImage = np.zeros(inputImages[0].shape)
    allImage = None
    for chunk in chunks(inputImages, columnSize):
        rowImage = None
        for imageIndex in range(columnSize):
            if imageIndex < len(chunk):
                rowImage = chunk[imageIndex] if imageIndex == 0 else np.concatenate(
                    (rowImage, chunk[imageIndex]), axis=1)
            else:
                rowImage = np.concatenate((rowImage, blankImage), axis=1)
        allImage = rowImage if allImage is None else np.concatenate(
            (allImage, rowImage), axis=0)
    return allImage


def chunks(l, n):
    # For item i in a range that is a length of l,
    for i in range(0, len(l), n):
        # Create an index range for l of n items:
        yield l[i:i+n]


def filterByAngle(lines, fromAngle, toAngle):
    return [line for line in lines if (line[0][3] - line[0][1]) / (line[0][2] - line[0][0]) >= fromAngle and (line[0][3] - line[0][1]) / (line[0][2] - line[0][0]) <= toAngle]


def computeAndShowLineAndEdges(inputImages, windowName):
    thrs1 = cv2.getTrackbarPos('Canny low', windowName)
    thrs2 = cv2.getTrackbarPos('Canny high', windowName)
    lineThreshold = cv2.getTrackbarPos('Hough Lines threshold', windowName)
    minLineLength = cv2.getTrackbarPos(
        'Hough Lines min line length', windowName)
    maxLineGap = cv2.getTrackbarPos(
        'Hough Lines max line gap', windowName)

    resultImages = []
    for inputImage in inputImages:
        (edge, lines) = findEdgesAndLines(inputImage, thrs1,
                                          thrs2, lineThreshold, minLineLength, maxLineGap)
        lines = filterByAngle(lines, -math.pi/3, 0)

        resultImages.append(getImageWithEdgedAndLines(inputImage, edge, lines))

    concatenatedResult = concatImages(resultImages, 5)

    cv2.imshow(windowName, concatenatedResult)


def selectLineDetectionThresholds(inputImages, windowName='edge', lowThres=2000, highThres=4000, houghThres=40, minLineLength=50, maxLineGap=10):
    cv2.namedWindow(windowName)
    cv2.createTrackbar('Canny low', windowName, lowThres, 5000, nothing)
    cv2.createTrackbar('Canny high', windowName, highThres, 5000, nothing)
    cv2.createTrackbar('Hough Lines threshold', windowName,
                       houghThres, 100, nothing)
    cv2.createTrackbar('Hough Lines min line length',
                       windowName, minLineLength, 100, nothing)
    cv2.createTrackbar('Hough Lines max line gap',
                       windowName, maxLineGap, 100, nothing)

    # computeAndShowLineAndEdges(windowName, inputImage)

    while True:
        computeAndShowLineAndEdges(inputImages, windowName)
        ch = cv2.waitKey(5)
        if ch == 27:
            break
    cv2.destroyWindow(windowName)
    print('Done')
