FROM python:3
ADD requirements.txt /
ADD service.py /
RUN pip install -r requirements.txt
CMD [ "python", "./service.py" ]