# This is API Docs

## post api

- POST `/api/post`
    - Content Type: `multipart/form-data`
    - Parameter
        - Image file
        - Text
    - Respond
      ```json5
       {
          "error": false,
          "message": "ok"
       }
      ```

- GET `/api/posts`
    - Respond
      ```json5
       {
          "error": false,
          "message": "ok",
          "data": [
             {
                "id": 123123,
                "time": 123123123,
                "text": "content",
                "image": "image path",
                "textImage": "text image path",
                "votingThreshold": true
             }
          ] 
       }
      ```

- GET `/api/post/{id}`
    - Respond
      ```json5
       {
           "error": false,
           "message": "ok",
           "data":{
               "id": 123123,
               "time": 123123123,
               "text": "content",
               "image": "image path",
               "textImage": "text image path",
               "votingThreshold": true
           }
       }
      ```

## Vote API

- GET `/api/votes`
    - Respond
      ```json5
       {
          "error": false,
          "message": "ok",
          "data": [
             {
                "id": 123123,
                "time": 123123123,
                "text": "content",
                "image": "image path",
                "textImage": "text image path",
                "votingThreshold": true
             }
          ] 
       }
      ```
