# -*- coding: utf-8 -*-
"""
Created on Mon Sep 19 20:01:30 2022

@author: Owner
"""

from PIL import Image
from pathlib import Path
#import albumentations as A
import cv2
import numpy as np
import matplotlib.pyplot as plt 
from scipy.sparse import csr_matrix
#import japanize_matplotlib japanize_matplotlib.japanize()
'''
#export
CANNY_THRESH_1 = 100
CANNY_THRESH_2 = 200
CONTAMI_RATIO=0.4
IMSIZE_FIND = 256  # 物体検出作業時の画像サイズ。巨大な画像の物体検出時に計算量を下げるため。
MARGIN = 0.05  # BOUNDING BOXの外周のマージン




def find_crop_point(img:Image)->(float,float,float,float):
    "物体を切り抜くxywhを求める"
    # 輪郭の抽出    
    gray = cv2.cvtColor(np.array(img),cv2.COLOR_BGR2GRAY)
    edges = cv2.Canny(gray, CANNY_THRESH_1, CANNY_THRESH_2)
    edges = cv2.dilate(edges, None)
    edges = cv2.erode(edges, None)
    contours, _ = cv2.findContours(edges, cv2.RETR_LIST, cv2.CHAIN_APPROX_NONE)

    # 輪郭線のタプルを点群に変換
    # tuple(ndarray(no, none, xy)) -> ndarray(point_no, (x,y))
    points = np.concatenate([x.reshape(-1,2) for x in contours], axis=0)

    # 異常値の除去
    clf = IsolationForest(
        contamination=CONTAMI_RATIO, # 異常値の割合
        max_features=2, random_state=42
    )
    clf.fit(points)
    y_pred = clf.predict(points)
    points_normal = points[y_pred==1, :]

    # 座標を求める
    x,y,w,h = cv2.boundingRect(points_normal)
    return x,y,w,h
            
            
   #export
def crop_find(img:Image)->Image:
    """ 元サイズ画像を、物体検出して切り抜き    
    1. 元画像(3024x4032)を、いったん256x256へ変換
    2. boxを計算
    3. 元画像をマージンを持って切り抜き
    """
    tmp = resize(img)
    # 物体検出
    x,y,w,h = find_crop_point(tmp)
    # left,...,bottomへ変換
    cx, cy = x+w//2, y+h//2  # Center of X
    l2 = int(max(w,h)/2*(1+MARGIN))  # 長辺の半分、ほぼ正方形に切り抜くため。
    l,t,r,b = (max(0,cx-l2),max(0,cy-l2),min(IMSIZE_FIND,cx+l2),min(IMSIZE_FIND,cy+l2))
    # 元画像での座標に変換
    h_original,w_original = img.size
    rx = w_original/IMSIZE_FIND  # 横幅比率
    ry = h_original/IMSIZE_FIND
    return img.crop((int(l*rx), int(t*ry), int(r*rx),int(b*ry)))     

#export
def draw_crop_rect(img:Image):
    x,y,w,h = find_crop_point(img)
    return cv2.rectangle(np.array(img),(x,y),(x+w,y+h),(0,255,0),2)
'''
def resize(img):
    height = img.shape[0]
    width = img.shape[1]
    w=500
    h=round(w/width*height)
    dst = cv2.resize(img,dsize=(w,h))
    return dst

def detect_contour(src):

  # 画像を読込
  #src = cv2.imread(path, cv2.IMREAD_COLOR)

  # グレースケール画像へ変換
  gray = cv2.cvtColor(src, cv2.COLOR_BGR2GRAY)

  # 2値化
  retval, bw = cv2.threshold(gray, 50, 255, cv2.THRESH_BINARY | cv2.THRESH_OTSU)

  # 輪郭を抽出
  #   contours : [領域][Point No][0][x=0, y=1]
  #   cv2.CHAIN_APPROX_NONE: 中間点も保持する
  #   cv2.CHAIN_APPROX_SIMPLE: 中間点は保持しない
  contours, hierarchy = cv2.findContours(bw, cv2.RETR_LIST, cv2.CHAIN_APPROX_NONE)

  # 矩形検出された数（デフォルトで0を指定）
  detect_count = 0

  # 各輪郭に対する処理
  for i in range(0, len(contours)):

    # 輪郭の領域を計算
    area = cv2.contourArea(contours[i])

    # ノイズ（小さすぎる領域）と全体の輪郭（大きすぎる領域）を除外
    if area < 1e2*5 or 1e5 < area:
      continue

    # 外接矩形
    if len(contours[i]) > 0:
      rect = contours[i]
      x, y, w, h = cv2.boundingRect(rect)
      cv2.rectangle(src, (x, y), (x + w, y + h), (0, 255, 0), 2)

      # 外接矩形毎に画像を保存
      #cv2.imwrite('output/' +filename +'/'+ str(detect_count) + '.jpg', src[y:y + h, x:x + w])

      detect_count = detect_count + 1
  # 外接矩形された画像を表示
  cv2.imshow('output', src)
  cv2.waitKey(0)

  # 終了処理
  cv2.destroyAllWindows()

def clipping(img):
    q_img_gray = cv2.cvtColor(q_img,cv2.COLOR_RGB2GRAY)
    
    #plt.imshow(q_img_gray, cmap='gray')
    
    
    th1, th2 = 100, 200
    edges = cv2.Canny(q_img_gray,  th1,th2)
    
    edges = cv2.dilate(edges, None)
    edges = cv2.erode(edges, None)
    
    plt.imshow(edges, cmap='gray')
    contours, _ = cv2.findContours(edges, cv2.RETR_LIST, cv2.CHAIN_APPROX_NONE)
    #print(len(contours), contours[5])
    
    points = np.concatenate([x.reshape(-1,2) for x in contours], axis=0)
    print(points.shape, points)
    
    contamination=0.4   # 外れ値の割合(0~0.5)
    from sklearn.ensemble import IsolationForest
    clf = IsolationForest(
        contamination=contamination, # 異常値の割合
        max_features=2, random_state=42
    )
    
    clf.fit(points)
    y_pred = clf.predict(points)
    
    plt.scatter(
        points[y_pred == -1, 0],
        points[y_pred == -1, 1],
        c='r', s=5,
        label='outlier'
    )
    plt.scatter(
        points[y_pred == 1, 0],
        points[y_pred == 1, 1],
        c='b', s=5,
        label='main',
    )
    plt.legend()
    plt.gca().set_aspect('equal', adjustable='box')
    plt.gca().invert_yaxis()
    plt.show()
    
    points_normal = points[y_pred==1, :]
    
    # 座標を求める
    x,y,w,h = cv2.boundingRect(points_normal)
    
    img_detected = cv2.rectangle(np.array(q_img),(x,y),(x+w,y+h),(0,255,0),2)
    plt.imshow(img_detected[:,:,[2,1,0]])
    

    
    img_croped = img_detected[y:y+h,x:x+w,:]
    #detect_contour(img_croped)
    return img_croped

filename = "crecore11"
q_img = cv2.imread("the_study/crecore11.jpg")
#q_img = cv2.imread("the_study/obj21.jpg")
#q_img = cv2.imread("the_study/stone05.jpg")
#q_img = cv2.imread("the_study/post01.jpg")
#q_img = cv2.imread("the_study/cute-dog.jpg")
#q_img = cv2.imread("the_study/obj04.jpg")
#q_img = cv2.imread("the_study/obj01.jpg")


q_img = resize(q_img)
img_croped = clipping(q_img)
    
plt.imshow(img_croped[:,:,[2,1,0]])
cv2.imshow("a",img_croped)
cv2.waitKey(0)
cv2.destroyAllWindows()


#detect_contour(img_croped)
