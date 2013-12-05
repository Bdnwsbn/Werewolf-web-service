import urllib2
import base64
import urllib

username= 'ben'
password= '1234'

request = urllib2.Request('http://host:8080/werewolf/players/alive')
base64string = base64.encodestring('%s: %s' % (username, password)).replace('\n', '')
request.add_header("Authorization", "Basic %s" % base64string)
result = urllib2.urlopen(request)
html = result.read()
print html