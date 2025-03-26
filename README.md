```
config localstack
aws --endpoint-url=http://localhost:4566 s3 ls s3://sorting-bucket/task-details/
aws configure
test
test
eu-central-1
json


post request: 
    endpoint: 
        http://localhost:8080/api/v1/submitTask
    example request:
        {
            "taskType": "Sort",
            "numberOfThreads": 5,
            "algorithmType": "Quick",
            "unsortedArray": [5, 3, 8, 1, 2, -10, 5, 3, 8, 1, 2, -10, 5, 3, 8, 1, 2, -10]
        }
    response:
        {uuid-of-task}

get request:
    endpoint: 
        http://localhost:8080/api/v1/getStatus?taskId={uuid-of-task}
    example response:
    {
        "taskResult": {
            "taskType": "Sort",
            "taskStatus": "Completed",
            "sortTimeMillis": 0,
            "sortedArray": [
                -10,
                -10,
                -10,
                1,
                1,
                1,
                2,
                2,
                2,
                3,
                3,
                3,
                5,
                5,
                5,
                8,
                8,
                8
            ]
        }
    }
```
