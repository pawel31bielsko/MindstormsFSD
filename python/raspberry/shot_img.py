from picamera import PiCamera
import sys
from select import select


timeout = 0.1
with  PiCamera(resolution='640x480') as camera:

    camera.start_preview()
    print('Saving images. Press any key to break.')
    
    i = 0
    while 1:
        rlist, wlist, xlist = select([sys.stdin], [], [], timeout)
        if rlist:
            break
        camera.capture('/home/pi/images/image%s.jpg' % i)
        i = i+1
        if i%10 == 0:
            print ('Pictures count: %s' % i) 
        