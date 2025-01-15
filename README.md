# File transfer

This project has been made in the context of [DAI](https://github.com/heig-vd-dai-course/) courses at HEIG-VD

## 1. Authors

- [Antoine Leresche](https://github.com/a2va)
- [Robin Forestier](https://github.com/forestierr)

## 2. Description

A simple file transfer application written in java.

### 2.1 How to use

`curl ...`

## 3. Get the project

Start by cloning the repository:

```bash
git clone https://github.com/forestier/dai-flappy-bird.git
```

Then, you can compile the project into a JAR located into the target folder:

```bash
./mvnw package
```

## 4. Docker

You can build the Docker image with the following command:
````bash
docker build -t ghcr.io/a2va/dai-file-transfer:latest .
````
Or using the prebuilt version from the GitHub Container Registry:
```bash
docker pull ghcr.io/a2va/dai-file-transfer:latest
```

Then you can run it with the following command:
```bash
docker run -d -p 8080:8080 ghcr.io/a2va/dai-file-transfer:latest
```

### 4.1 Docker Compose

Once you cloned the repository, you can deploy the application using Docker Compose.
For that you first need to create a `.env` file in the root of the project with the following content:
```
FILETRANSFER_DOMAIN_NAME=filetransfer.example.com

TRAEFIK_FULLY_QUALIFIED_DOMAIN_NAME=traefik.example.com
TRAEFIK_ACME_EMAIL=your@email.com
TRAEFIK_ENABLE_DASHBOARD=true
```
Modify each of the variables to match your needs, then go to your domain provider 
and add a record pointing to the IP address of your server.

To launch the application with Docker Compose, simply run the following command:
```docker compose up -d```

## 5. API

The API allows you to upload and download files.

The json format is mainly used for the API.

The API is based on the CRUD operations :
- Upload a file
- Download a file
- Delete a file

### 5.1 Endpoints

#### 5.1.1 Upload a file

- `PUT /{filename}` 

Upload a file, this request will automatically redirect to the upload endpoint.
`/upload/{filename}`

**Request**

- `filename` (string): the name of the file

#### 5.1.2 Upload a file

- `PUT /upload/{filename}`

Put the file with a specific name, the server will respond with a code to authenticate the user to modify/delete the file later and an ID.

**Request**

The request body must contain the file to upload.

**Response**

???????
- `downloadId` (string): the ID of the file
- `authCode` (string): the code to authenticate the user

**Status codes**

- `200 (OK)`: the file has been uploaded
- `400 (Bad Request)`: no file provided

#### 5.1.3 Download a file

- `GET /download/{id}`

Download the file with the following download ID.

**Request**

- `id` (string): the download ID of the file

**Response**

The response body contains the file to download.

**Status codes**

- `200 (OK)`: the file has been downloaded
- `400 (Bad Request)`: no id provided
- `404 (Not Found)`: file not found

#### 5.1.4 Rename a file

- `PATCH /rename/{id}`

**Request**

- `id` (string): the ID of the file
- `filename` (string): the new name of the file

**Response**

??????

**Status codes**

- `200 (OK)`: the file has been renamed
- `400 (Bad Request)`: no id provided
- `401 (Unauthorized)`: the code is invalid
- `404 (Not Found)`: file not found

#### 5.1.5 Modify a file

- `PATCH /modify/{id}`

Replace the file with the following ID by a new file. 
Without modifying the ID or the download ID.
The download id stays the same but the authentication code will be changed.

**Request**

- `id` (string): the ID of the file

**Response**

- `downloadId` (string): the ID of the file
- `authCode` (string): the new code to authenticate the user


**Status codes**

- `200 (OK)`: the file has been modified
- `400 (Bad Request)`: no id provided
- `401 (Unauthorized)`: the code is invalid
- `404 (Not Found)`: file not found

#### 5.1.6 Delete a file

- `DELETE /delete/{id}`

Delete the file with the following ID. The user must provide the authentication code give by the server when the file was uploaded.

**Request**

- `id` (string): the ID of the file

**Response**

??????

**Status codes**

- `200 (OK)`: file deleted
- `400 (Bad Request)`: no id provided
- `401 (Unauthorized)`: the code is invalid
- `404 (Not Found)`: file not found

## 6. Server

...

### 6.1 Set up the server

The README (or repository) contains instructions how to install and configure the virtual machine with each step

### 6.2 Use DNS to have a domain name

The README (or repository) contains explains how to configure the DNS zone to access your web application

### 6.3 Run the app on the server

The README (or repository) contains instructions how to deploy, run and access the web applications with Docker Compose

### 6.4 Try it

The README displays the domain names configuration in the DNS zone to validate everything is set up right