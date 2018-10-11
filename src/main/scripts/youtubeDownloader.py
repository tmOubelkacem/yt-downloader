from pytube import YouTube
import os
import sys

videoUrl = sys.argv[1]
path = sys.argv[2]

print("Start downloading >>>> "+videoUrl)
yt = YouTube(videoUrl)
yt = yt = yt.streams.filter(progressive=True, file_extension='mp4').order_by('resolution').desc().first()
if not os.path.exists(path):
        os.makedirs(path)
yt.download(path)
print("... downloaded")

# installer : pip install pytube
# run : python <filename>.py
