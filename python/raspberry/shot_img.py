from picamera import PiCamera
from time import sleep

with  PiCamera(resolution='640x480') as camera:

    camera.start_preview()
    print('Saving images. Press enter to break.')
    
    i = 0
    while 1:
        sleep(0.1)
        camera.capture('/home/pi/images/image%s.jpg' % i)
        i = i+1
        if i%10 == 0:
            print ('Pictures count: %s' % i) 
        