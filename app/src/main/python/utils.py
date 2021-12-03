
import cv2
import numpy as np
import imutils
import numpy as np
from imutils import contours, perspective
from imutils.video import FPS, VideoStream
from scipy.spatial import distance as dist
from com.chaquo.python import Python
import os
from scipy import io
from io import BytesIO


def fun1(byteFrame, r, c, ch):
    context = Python.getPlatform().getApplication()
    # frame=np.array(frame)
    # f = cv2.CreateMat(frame)
    # frame = np.asarray( frame[:,:] )
    # return type(byteFrame)
    # np_bytes = BytesIO(byteFrame)
    # frame = np.load(np_bytes, allow_pickle=True)
    frame = np.array(byteFrame, dtype=np.uint8)
    frame.resize(c, r, ch)
    orig = frame.copy()
    # return frame.shape[0]*frame.shape[1]*frame.shape[2]
    # frame = io.loadmat(frame)
                   # ,dtype=np.uint8)
    # np_bytes = frame.tobytes()

    # np_bytes = BytesIO()
    # np.save(np_bytes, frame, allow_pickle=True)
    # np_bytes = frame.tobytes()
    # return np_bytes
    # return np_bytes.getvalue()

    # return frame[0:100, 0:100, 0]
    # frame_bgr1=np.reshape(frame_bgr1, (r,c,ch))


    # frame_gray=None
    # frame_gray=cv2.cvtColor(frame_bgr1, cv2.COLOR_BGR2GRAY)

    # if frame_gray is not None:
    #     return "SUCCESS"
    # else:
    #     return "FAIL"

    basepath = str(context.getDataDir().getAbsolutePath())
               # + '/models/'

    # return basepath


    prototxt = basepath + "/mobilenetssd_deploy.prototxt.txt"
    model = basepath + "/mobilenetssd_deploy.caffemodel"

    net = cv2.dnn.readNet(
        # args["prototxt"],
        prototxt,
        # args["model"],
        model
    )

    frame = imutils.resize(frame, width=1000)

    # grab the frame dimensions and convert it to a blob
    (h, w) = frame.shape[:2]
    blob = cv2.dnn.blobFromImage(
        cv2.resize(frame, (300, 300)), 0.007843, (300, 300), 127.5
    )

    # pass the blob through the network and obtain the detections and
    # predictions

    net.setInput(blob)
    detections = net.forward()
    for i in np.arange(0, detections.shape[2]):
        confidence = detections[0, 0, i, 2]
        if confidence > 0.2:
            idx = int(detections[0, 0, i, 1])
            box = detections[0, 0, i, 3:7] * np.array([w, h, w, h])
            (startX, startY, endX, endY) = box.astype("int")


    ################## Size Prediction #################################
    # load the image, convert it to grayscale, and blur it slightly
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    gray = cv2.GaussianBlur(gray, (7, 7), 0)
    # perform edge detection, then perform a dilation + erosion to
    # close gaps in between object edges
    edged = cv2.Canny(gray, 50, 100)
    edged = cv2.dilate(edged, None, iterations=1)
    edged = cv2.erode(edged, None, iterations=1)
    # find contours in the edge map
    cnts = cv2.findContours(edged.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    cnts = cnts[0]
    # sort the contours from left-to-right and initialize the
    # 'pixels per metric' calibration variable
    prev_cnts = None
    try:
        (cnts, _) = contours.sort_contours(cnts)
        prev_cnts = cnts
    except ValueError:
        cnts = prev_cnts
    # (cnts, _) = contours.sort_contours(cnts)
    pixelsPerMetric = None

    # loop over the contours individually
    # cv2.circle(orig, (0, 0), 500, (0, 0, 255), -1)
    # cv2.rectangle(orig, (0, 0), (1000, 1000), (800, 132, 109), -1)
    if cnts is not None:
        for c in cnts:
            # if the contour is not sufficiently large, ignore it
            if cv2.contourArea(c) < 100:
                continue
            # compute the rotated bounding box of the contour
            # orig = frame.copy()
            box = cv2.minAreaRect(c)
            box = cv2.cv.BoxPoints(box) if imutils.is_cv2() else cv2.boxPoints(box)
            box = np.array(box, dtype="int")

            # ordering the points in the contour such that they appear
            # in top-left, top-right, bottom-right, and bottom-left
            # order, then draw the outline of the rotated bounding
            # box
            box = perspective.order_points(box)
            cv2.drawContours(orig, [box.astype("int")], -1, (0, 255, 0), 2)

            # loop over the original points and draw them
            for (x, y) in box:
                cv2.circle(orig, (int(x), int(y)), 5, (0, 0, 255), -1)



            # unpacking the ordered bounding box, then compute the midpoint
            # between the top-left and top-right coordinates, followed by
            # the midpoint between bottom-left and bottom-right coordinates

            (tl, tr, br, bl) = box
            (tltrX, tltrY) = midpoint(tl, tr)
            (blbrX, blbrY) = midpoint(bl, br)

            # calculating the midpoint between the top left and right upper points,
            # then midpoint right upper and lower right point
            (tlblX, tlblY) = midpoint(tl, bl)
            (trbrX, trbrY) = midpoint(tr, br)

            # draw the midpoints on the image
            cv2.circle(orig, (int(tltrX), int(tltrY)), 5, (255, 0, 0), -1)
            cv2.circle(orig, (int(blbrX), int(blbrY)), 5, (255, 0, 0), -1)
            cv2.circle(orig, (int(tlblX), int(tlblY)), 5, (255, 0, 0), -1)
            cv2.circle(orig, (int(trbrX), int(trbrY)), 5, (255, 0, 0), -1)

            # draw lines between the midpoints
            cv2.line(
                orig, (int(tltrX), int(tltrY)), (int(blbrX), int(blbrY)), (255, 0, 255), 2
            )
            cv2.line(
                orig, (int(tlblX), int(tlblY)), (int(trbrX), int(trbrY)), (255, 0, 255), 2
            )

            # compute the Euclidean distance between the midpoints
            dA = dist.euclidean((tltrX, tltrY), (blbrX, blbrY))
            dB = dist.euclidean((tlblX, tlblY), (trbrX, trbrY))

            # if args["unit"] == "cm":
                # converting to cm
            if True:
                dimA = dA * 0.026458
                dimB = dB * 0.026458
                dimC = dimA * dimB

                # draw the object sizes on the image
                cv2.putText(
                    orig,
                    "{:.1f}cm".format(dimA),
                    (int(tltrX - 15), int(tltrY - 10)),
                    cv2.FONT_HERSHEY_SIMPLEX,
                    0.65,
                    (0, 0, 0),
                    2,
                )
                cv2.putText(
                    orig,
                    "{:.1f}cm".format(dimB),
                    (int(trbrX + 10), int(trbrY)),
                    cv2.FONT_HERSHEY_SIMPLEX,
                    0.65,
                    (0, 0, 0),
                    2,
                )

                # output text
                # font = cv2.FONT_HERSHEY_SIMPLEX
                # cv2.rectangle(orig, (1000, 1000), (700, 620), (800, 132, 109), -1)
                # cv2.putText(
                #     orig,
                #     "Volume: " + "{:.2f} m^2".format(dimC),
                #     (700, 650),
                #     font,
                #     0.7,
                #     (0xFF, 0xFF, 0x00),
                #     1,
                #     cv2.FONT_HERSHEY_SIMPLEX,
                #     )

                # cv2.putText(
                #     orig,
                #     "Orig Length: ",
                #     # + format(args["length"]),
                #     (700, 690),
                #     font,
                #     0.7,
                #     (0xFF, 0xFF, 0x00),
                #     1,
                #     cv2.FONT_HERSHEY_SIMPLEX,
                #     )
                # cv2.putText(
                #     orig,
                #     "Orig Width: " ,
                #     # + format(args["width"]),
                #     (700, 730),
                #     font,
                #     0.7,
                #     (0xFF, 0xFF, 0x00),
                #     1,
                #     cv2.FONT_HERSHEY_SIMPLEX,
                #     )


            # elif args["unit"] == "m":
            #     # converting to meters
            #     dimA = dA * 0.000264583
            #     dimB = dB * 0.000264583
            #     dimC = dimA * dimB
            #     cv2.putText(
            #         orig,
            #         "{:.1f}m".format(dimA),
            #         (int(tltrX - 15), int(tltrY - 10)),
            #         cv2.FONT_HERSHEY_SIMPLEX,
            #         0.65,
            #         (255, 255, 255),
            #         2,
            #     )
            #     cv2.putText(
            #         orig,
            #         "{:.1f}m".format(dimB),
            #         (int(trbrX + 10), int(trbrY)),
            #         cv2.FONT_HERSHEY_SIMPLEX,
            #         0.65,
            #         (255, 255, 255),
            #         2,
            #     )
            #     # output text
            #     font = cv2.FONT_HERSHEY_SIMPLEX
            #
            #     cv2.rectangle(orig, (1500, 1600), (700, 620), (800, 132, 109), -1)
            #     cv2.putText(
            #         orig,
            #         "Volume: " + "{:.2f} m^2".format(dimC),
            #         (700, 650),
            #         font,
            #         0.7,
            #         (0xFF, 0xFF, 0x00),
            #         1,
            #         cv2.FONT_HERSHEY_SIMPLEX,
            #         )
            #
            #     cv2.putText(
            #         orig,
            #         "Orig Length: " + format(args["length"]),
            #         (700, 690),
            #         font,
            #         0.7,
            #         (0xFF, 0xFF, 0x00),
            #         1,
            #         cv2.FONT_HERSHEY_SIMPLEX,
            #         )
            #     cv2.putText(
            #         orig,
            #         "Orig Width: " + format(args["width"]),
            #         (700, 730),
            #         font,
            #         0.7,
            #         (0xFF, 0xFF, 0x00),
            #         1,
            #         cv2.FONT_HERSHEY_SIMPLEX,
            #         )

    np_bytes = orig.tobytes()
    return np_bytes


# initializing midpoint
def midpoint(ptA, ptB):
    return ((ptA[0] + ptB[0]) * 0.5, (ptA[1] + ptB[1]) * 0.5)



