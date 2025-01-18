# File transfer

This project has been made in the context of [DAI](https://github.com/heig-vd-dai-course/) courses at HEIG-VD

## 1. Authors

- [Antoine Leresche](https://github.com/a2va)
- [Robin Forestier](https://github.com/forestierr)

## 2. Description

A simple file transfer application written in java.

### 2.1 How to use it with cURL

#### 2.1.1 Upload a file

`curl -X PUT -F "file=@/path/to/your/file" http://localhost:8080/upload/{filename}`

The server will respond with a code to authenticate the user to modify/delete the file later and an fileId
that will be used to download the file.

For simplicity, you can also do the upload in the following way:
```bash
curl -T path/to/your/file -L - localhost:8080
```

#### 2.1.2 Download a file
```bash
curl -X GET http://localhost:8080/download/{id} -o file.txt
```

Using the fileId as `{id}`.

#### 2.1.3 Modify a file
```bash
`curl -X PATCH -F "file=@/path/to/your/file" http://localhost:8080/modify/{id}?authCode={code}`
```
Using the fileId as `{id}` and the code provided by the server when the file was uploaded as `{code}`.

#### 2.1.4 Delete a file
```bash
`curl -X DELETE http://localhost:8080/delete/{id}?authCode={code}`
```
Using the fileId as `{id}` and the code provided by the server when the file was uploaded as `{code}`.

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

To learn how to use the docker compose file, you can go to [Deployment/Docker Compose](#63-docker-compose)

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

- `fileId` (string): the ID of the file
- `authCode` (string): the code to authenticate the user

**Status codes**

- `200 (OK)`: the file has been uploaded
- `400 (Bad Request)`: no file provided
- `409 (Conflict)`: the file already exists

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

#### 5.1.5 Modify a file

- `PATCH /modify/{id}`

Replace the file with the following ID by a new file. 
Without modifying the ID or the download ID.
The download id stays the same but the authentication code will be changed.

**Request**

- `id` (string): the ID of the file

**Response**

- `fileId` (string): the ID of the file
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

- String: File deleted

**Status codes**

- `200 (OK)`: file deleted
- `400 (Bad Request)`: no id provided
- `401 (Unauthorized)`: the code is invalid
- `404 (Not Found)`: file not found

## 6. Deployment

### 6.1 Set up the server

In the context of the DAI course, the service was deployed on an Azure virtual machine
with the free credit provided by the Azure for Students program.

To create a virtual machine, you head to [Azure](https://azure.microsoft.com/) and follows this steps:
1. Create a new virtual machine from the `Create a ressource` section
2. Select the following VM specs:
   * Region: (Europe) West Europe
   * Availability options: No infrastructure redundancy required
   * Security type: Trusted launch virtual machines (the default)
   * Image: Ubuntu Server 24.04 LTS - x64 Gen2 (the default)
   * VM architecture: x64
   * Size: Standard_B1s - you might need to click "See all sizes" to see this option
   * Public inbound ports: Allow selected ports
   * Select inbound ports: HTTP (80), HTTPS (443), SSH (22)

The username was named `ubuntu` and the SSH public key was already existing.
If you want more information, you can head to [DAI - Acquire a virtual machine on a cloud provider](https://github.com/heig-vd-dai-course/heig-vd-dai-course/blob/5df80f64121b35bed5adc5f3d486d90ee2b1344b/20-ssh-and-scp/COURSE_MATERIAL.md#acquire-a-virtual-machine-on-a-cloud-provider)

### 6.2 Configure the DNS zone

You need to configure the DNS zone to access your web application, for that go to your domain provider and add a record pointing to the IP address of your server.
If you don't have a domain, you follow these [steps](https://github.com/heig-vd-dai-course/heig-vd-dai-course/blob/main/22-web-infrastructures/COURSE_MATERIAL.md#obtain-a-domain-name) from the DAI course.

In my case my domains is on cloudflare, so head to the [Cloudflare Dashboard](https://dash.cloudflare.com), and click 
on the domain you want to use
1. Go to DNS -> Records
2. Add two type A records, that point to the IP address of your server (It's displayed on the configuration on page of the VM in Azure).
   * `transfer`
   * `traefik`


### 6.3 Docker Compose

Once you cloned the repository on the VM, you can deploy the application using Docker Compose.
For that you first need to create a `.env` file in the root of the project with the following content:
```
FILETRANSFER_DOMAIN_NAME=transfer.example.com

TRAEFIK_FULLY_QUALIFIED_DOMAIN_NAME=traefik.example.com
TRAEFIK_ACME_EMAIL=your@email.com
TRAEFIK_ENABLE_DASHBOARD=true
```
Modify each of the variables to match your needs, then go to your domain provider
and add a record pointing to the IP address of your server.

To launch the application with Docker Compose, simply run the following command:
```docker compose up -d```

### 6.4 Try it

This project is deployed on the following domain:
- `transfer.a2va.dev`
- `traefik.a2va.dev`