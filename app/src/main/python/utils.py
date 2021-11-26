
import cv2
import numpy as np
import imutil

def fun1(frame_bgr,r,c,ch):

    frame_bgr1=np.array(frame_bgr,dtype=np.uint8)
    frame_bgr1=np.reshape(frame_bgr1, (r,c,ch))


    frame_gray=None
    frame_gray=cv2.cvtColor(frame_bgr1, cv2.COLOR_BGR2GRAY)

    if frame_gray is not None:
        return "SUCCESS"
    else:
        return "FAIL"