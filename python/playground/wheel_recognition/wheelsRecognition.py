from wheelRecognition import WheelRecognition
from select_threshold_lines import getImageWithEdgedAndLines, filterByAngle, angleOfLine, angleToDeg


class WheelsRecognition(object):
    def __init__(self, cannyParams, houghParams, roiLeft, roiRight, angleRangeLeftWheel, angleRangeRightWheel):
        self.leftWheel = WheelRecognition(
            cannyParams, houghParams, angleRangeLeftWheel)
        self.rightWheel = WheelRecognition(
            cannyParams, houghParams, angleRangeRightWheel)
        self.roiLeft = roiLeft
        self.roiRight = roiRight
        self.anyWheelFound = 0
        self.imagesProcessed = 0

    def findWheelsLines(self, image):
        leftLines = self.leftWheel.findLinesInROI(
            image, self.roiLeft[0], self.roiLeft[1])
        rightLines = self.rightWheel.findLinesInROI(
            image, self.roiRight[0], self.roiRight[1])
        if len(leftLines) > 0 or len(rightLines) > 0:
            self.anyWheelFound += 1
        self.imagesProcessed += 1
        return (leftLines, rightLines)

    def resetStatistic(self):
        self.anyWheelFound = 0
        self.imagesProcessed = 0

    def formatStatistic(self, lines):
        leftAngle = angleToDeg(angleOfLine(lines[0][0]))
        rightAngle = angleToDeg(angleOfLine(lines[1][0]))
        diff = abs(leftAngle-rightAngle)

        return 'Wheels angle left: {:3.2f} right: {:3.2f} diff: {:3.2f} Accuracy: {:2.2f} %'.format(
            leftAngle, rightAngle, diff, self.anyWheelFound/self.imagesProcessed * 100)
