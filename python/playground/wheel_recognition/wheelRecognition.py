from line_recognition import findEdgesAndLines
from select_threshold_lines import filterByAngle

class WheelRecognition(object):
    def __init__(self, cannyParams, houghParams, angleRange):
        self.cannyParams = cannyParams
        self.houghParams = houghParams
        self.angleRange = angleRange

    def findLinesInROI(self, image, roiY, roiX):
        roiImage = image[roiY[0]:roiY[1], roiX[0]:roiX[1]]
        (edge, lines) = findEdgesAndLines(
            roiImage, self.cannyParams, self.houghParams)

        lines = filterByAngle(lines, self.angleRange[0], self.angleRange[1])
        
        return [[(line[0][0] + roiX[0], line[0][1] + roiY[0], line[0][2] + roiX[0], line[0][3] + roiY[0])] for line in lines]
