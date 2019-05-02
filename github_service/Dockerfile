FROM python:3.6
ADD requirements.txt /
ADD service.py /
RUN pip install -r requirements.txt
CMD [ "python", "-u", "service.py" ]