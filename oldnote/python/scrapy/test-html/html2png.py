#!/usr/bin/python

import pdfcrowd
import sys

try:
    # create the API client instance
    client = pdfcrowd.HtmlToImageClient('your_username', 'your_apikey')

    # configure the conversion
    client.setOutputFormat('png')

    # run the conversion and write the result to a file
    client.convertUrlToFile('http://www.example.com', 'example.png')
except pdfcrowd.Error as why:
    # report the error
    sys.stderr.write('Pdfcrowd Error: {}\n'.format(why))

    # handle the exception here or rethrow and handle it at a higher level
    raise


