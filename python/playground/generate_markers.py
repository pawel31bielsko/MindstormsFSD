import cv2
d = cv2.aruco.getPredefinedDictionary(0)
for i in range(20,30):
    image = cv2.aruco.drawMarker(d,i,118);
    cv2.imwrite('marker%s.png' % i, image)

