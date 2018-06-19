#!/usr/bin/python

import pdfkit

def parse_url_to_html(url):
    response = requests.get(url)
    soup = BeautifulSoup(response.content, "html5lib")
    body = soup.find_all(class_="x-wiki-content")[0]
    html = str(body)
    with open("a.html", 'wb') as f:
        f.write(html)

#pdfkit.from_url('file:///Users/chenyansong/Documents/note/oldnote/python/scrapy/test-html/in.htm', 'out.pdf')

pdfkit.from_url('https://www.baidu.com/', 'out.pdf')

