[![Java CI](https://github.com/umjammer/vavi-apps-webdav/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/vavi-apps-webdav/actions/workflows/maven.yml)
[![CodeQL](https://github.com/umjammer/vavi-apps-webdav/actions/workflows/codeql-analysys.yml/badge.svg)](https://github.com/umjammer/vavi-apps-webdav/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-25-b07219)

# ![Cloud Storage icon by Icons8](https://img.icons8.com/nolan/64/cloud-storage.png) vavi-apps-webdav

Gathering any kind of cloud storages into one webdav strage!

### Image

```
vavi-apps-webdav +
                 +- googledrive1
                 +- googledrive2
                 +- onedrive
                 +- dropbpx
                 +- box
                 :
```

## Installation

 * [Running Locally](https://github.com/umjammer/vavi-apps-webdav/wiki/RunningLocally)
 * [Deploying to Heroku](https://github.com/umjammer/vavi-apps-webdav/wiki/DeployingToHeroku)

## Usage

### Admin

access https://your-heroku-app.herokuapp.com/admin/list

https basic authentication: enter admin/${WEBDAV_ADMIN_PASSWORD}

### User

access https://your-heroku-app.herokuapp.com/webdav using webdav client

https basic authentication: enter user/${WEBDAV_USER_PASSWORD}

## References

* https://qiita.com/suke_masa/items/908805dd45df08ba28d8
* https://b1san-blog.com/post/spring/spring-sec-auth/

### Competitor

 * https://github.com/a-langer/webdav-vfs-gate

## TODO

 * googledrive refresh doesn't work?
 * onedrive refresh doesn't work?
 * jackrabbit 3.x.x supported jakarta ee is blocking

----
<sub>
<a href="https://icons8.com/icon/44790/cloud-storage">Cloud Storage icon by Icons8</a>
</sub>