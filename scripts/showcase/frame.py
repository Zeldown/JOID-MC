"""Frames the showcase captures of a game window like a desktop screen: the title bar of titlebar.png above each capture.

usage: python3 frame.py <showcase output folder>
"""
import glob
import os
import sys

from PIL import Image

TITLEBAR = Image.open(os.path.join(os.path.dirname(os.path.abspath(__file__)), "titlebar.png")).convert("RGB")


def main():
	for path in glob.glob(os.path.join(sys.argv[1], "**", "*.png"), recursive=True):
		capture = Image.open(path).convert("RGB")
		if capture.height == TITLEBAR.height + 1080:
			continue
		screen = Image.new("RGB", (capture.width, TITLEBAR.height + capture.height))
		screen.paste(TITLEBAR, (0, 0))
		screen.paste(capture, (0, TITLEBAR.height))
		screen.save(path)


if __name__ == "__main__":
	main()
