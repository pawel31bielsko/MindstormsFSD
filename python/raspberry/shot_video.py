import picamera


with picamera.PiCamera(resolution='640x480', framerate=24) as camera:
    camera.start_preview()
    camera.start_recording('/home/pi/Desktop/video.h264')
    print('Recording started.')    
    input("Press ENTER key to stop recording and save.")    
    camera.stop_recording()
    camera.stop_preview()

    

    