# -*- coding: utf-8 -*-
"""
Created on Sun Sep 18 17:03:31 2022

@author: hiram
"""

def culcScore(color_thres:float,color_score:float,char_thres:float,char_score:float):
    thres_score = 60
    zero_border = 0.45
    
    if color_score<=color_thres and color_score>=0:
        color_result = -((thres_score-100)/-color_thres**2)*color_score**2+100
        #(thres_score-100)*(color_thres**2)+100
    elif color_score>color_thres and color_score<=zero_border:
        print("あああ")
        color_result =(thres_score/(color_thres-zero_border)**2)* (color_score-zero_border)**2
        #thres_score/(color_thres**2-zero_border)*(color_score**2-zero_border)
    else:
        color_result = 0
    
    if char_score<char_thres and char_score>=0:
        char_result = (thres_score-100)*char_thres**2+100
    elif char_score>char_thres and char_score<=zero_border:
        #間違っているから上見て直せ
        #char_result = thres_score/(char_thres**2-zero_border)*(char_score**2-zero_border)
        char_result = 0
    else:
        char_result = 0
    
    #color_result=round(color_result,2)
    #char_result=round(char_result,2)
    return color_result,char_result


a,b = culcScore(0.25, 0.3, 0.05, 0.29900840)
print(a)
print(b)