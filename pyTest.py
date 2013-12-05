import requests
from requests.auth import HTTPBasicAuth
import json

username = 'ben'
password = '1234'

#r = requests.get('http://localhost:8080/admin/restartGame', 
#				auth=HTTPBasicAuth(username, password))			
#print(r.text)


r = requests.get('http://localhost:8080/players/alive', 
				auth=HTTPBasicAuth(username, password))				
print(r.text)


r = requests.get('http://localhost:8080/players/nearby', 
				auth=HTTPBasicAuth(username, password))				
print(r.text)


r = requests.get('http://localhost:8080/players/votable', 
				auth=HTTPBasicAuth(username, password))				
print(r.text)

# What to do for /{id} ? Have some user in database added already?

#r = requests.post('http://localhost:8080/players/votes/{id}', 
#				auth=HTTPBasicAuth(username, password))		
#print(r.text)


#r = requests.post('http://localhost:8080/players/kill/{id}', 
#				auth=HTTPBasicAuth(username, password))	
#print(r.text)


payload = {'lat': 42.3, 'lng': 30.2}
r = requests.post('http://localhost:8080/location/1', 
				auth=HTTPBasicAuth(username, password), data=payload)
print(r.text)