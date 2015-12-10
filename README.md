# TwitterSentiment

Twitter API (Java Library) - https://github.com/twitter/hbc
Twitter API (Firehose) - https://dev.twitter.com/streaming/reference/get/statuses/firehose
Consumer Key (API Key)euoX2XoXH9Q5WY4AzVwitVdZf
Consumer Secret (API Secret)1bxT0CSIcWdNOg9pUhf7wPtsyCzLK99kd7lXRHEdZyK2HgaSs1
Rate Limit: 180 per 15 minutes

GIS library - https://www.mapbox.com/
Sentiment Analysis - http://www.alchemyapi.com/api/sentiment/textc.html
Play Framework - https://www.playframework.com/
Spark Computing - http://spark.apache.org/
ML stuff - http://www.scalanlp.org/ , http://spark.apache.org/docs/latest/mllib-guide.html 

notes: https://dev.twitter.com/streaming/overview/processing
twitter api says that for streaming, there should be a process that puts raw message text into a queue the moment they are received, and a seperate process should be responsible for parsing the messages. 

we will need to be tolerant of duplicate messages. 
it is possible to request a zipped stream of data.

we will need to sign up for mapbox. their geocaching web service seems useful, and we could probably even plot this to one of their maps
 
three major components: continous stream of tweets, filtered by US, sentiment analysis of tweets, displaying live map, with color highlights based on average sentiment

(we should represent positive sentiment with red, neutral tweets green, and negative sentment with blue. or something like that so we can represent the various prevalence)
plan of action:
1.intialize git repository
2. write code to analyze sentiment of one tweet
3. work with mapbox to display map, highlight a given state(Luke)
4. highlight given state with a color scale(Luke)
5. grab one tweet, and convert it lattittude and longitude to a state 
6. scrape fifty tweets, and display on map
7. create server to continously stream tweets
8. use seperate processes to load tweets, and to run sentiment analysis on tweets in the queue and then to display them on the map.
