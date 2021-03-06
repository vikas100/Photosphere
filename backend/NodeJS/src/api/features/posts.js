import uuid from 'uuid/v1'
import { Response, Errors, Utils } from '../helper'
import { Post } from '../models'

export const Validator = {

    createPost() {
        return (req, res, next) => {
            if (!req.body.title) {
                Utils.deleteObjectsS3(req.file.key, (err, data) => {
                    if (err)
                        console.log(err.message)
                    console.log(data)
                })
                return Response.sendError(res, Errors.NoTitle)
            }

            if (!req.body.description) {
                Utils.deleteObjectsS3(req.file.key, (err, data) => {
                    if (err)
                        console.log(err.message)
                    console.log(data)
                })
                return Response.sendError(res, Errors.NoDescription)
            }

            next()
        }
    },

    createComment() {
        return (req, res, next) => {
            if (!req.body.comment)
                return Response.sendError(res, Errors.NoComment)
            next()
        }
    },

    sorter() {
        return (req, res, next) => {
            let skip = 0, limit = 10, orderBy = 'updatedAt', sortOrder = -1

            if (req.query.skip)
                skip = Math.max(0, req.query.skip)

            if (req.query.limit)
                limit = Math.min(Math.max(1, req.query.limit), 10)

            let tempOrderBy = req.query.orderBy
            let tempSortOrder = req.query.sortOrder

            if (tempSortOrder === 'asc')
                tempSortOrder = 1
            else
                tempSortOrder = -1

            if (tempOrderBy === 'createdAt' || tempOrderBy === 'updateAt') {
                // Custom created or updated date ordering. Only descending allowed as it is indexed so
                orderBy = tempOrderBy
                sortOrder = -1
            } else if (tempOrderBy === 'likesCount' || tempOrderBy === 'commentsCount') {
                // Custom, supports ascending and descending ordering
                orderBy = tempOrderBy
                sortOrder = tempSortOrder
            }

            req.query.orderBy = orderBy
            req.query.sortOrder = sortOrder
            next()
        }
    },

    paginator(def, max) {
        return (req, res, next) => {
            let skip = 0, limit = def

            if (req.query.skip)
                skip = Math.max(0, req.query.skip)

            if (req.query.limit)
                limit = Math.min(Math.max(1, req.query.limit), max)

            req.query.skip = skip
            req.query.limit = limit
            next()
        }
    },

    editPostTitle() {
        return (req, res, next) => {
            if (!req.body.title)
                return Response.sendError(res, Errors.NoTitle)
            next()
        }
    },

    editPostDescription() {
        return (req, res, next) => {
            if (!req.body.description)
                return Response.sendError(res, Errors.NoDescription)
            next()
        }
    },

    editComment() {
        return (req, res, next) => {
            if (!req.body.comment)
                return Response.sendError(res, Errors.NoComment)
            next()
        }
    }

}

export const Endpoint = {

    createPost() {
        return (req, res) => {
            Post.createPost(req.user._id, req.user.name, req.body.title, req.body.description, req.imageAspectRatio, req.file.key, (err, post) => {

                if (err || !post) {
                    if (err)
                        console.log(err.message)
                    Utils.deleteObjectsS3(req.file.key, (err, data) => {
                        if (err)
                            console.log(err.message)
                        console.log(data)
                    })
                    return Response.sendError(res, Errors.Internal)
                }

                Response.send(res, post)
            })
        }
    },

    createComment() {
        return (req, res) => {
            Post.createComment(req.user._id, req.params.postId, req.body.comment, (err, post) => {

                if (err) {
                    console.log(err.message)
                    return Response.sendError(res, Errors.Internal)
                }

                if (!post)
                    return Response.sendError(res, Errors.NotFound)

                Response.send(res, post.comments[0])

            })
        }
    },

    likePost() {
        return (req, res) => {
            Post.like(req.user._id, req.params.postId, (err, post) => {

                if (err) {
                    console.log(err.message)
                    return Response.sendError(res, Errors.Internal)
                }

                if (!post)
                    return Response.sendError(res, Errors.NotFound)

                Response.send(res)

            })
        }
    },

    getAllPosts() {
        return (req, res) => {
            Post.getAllPosts(req.user._id, req.query.skip, req.query.limit, req.query.orderBy, req.query.sortOrder, (err, posts) => {

                if (err) {
                    console.log(err.message)
                    return Response.sendError(res, Errors.Internal)
                }

                if (!posts)
                    return Response.sendError(res, Errors.NotFound)

                Response.send(res, posts)
            })
        }
    },

    getAllPostsByUser() {
        return (req, res) => {
            Post.getAllPostsByUser(req.user._id, req.query.userId, req.query.skip, req.query.limit, req.query.orderBy, req.query.sortOrder, (err, posts) => {

                if (err) {
                    console.log(err.message)
                    return Response.sendError(res, Errors.Internal)
                }

                if (!posts)
                    return Response.sendError(res, Errors.NotFound)

                Response.send(res, posts)
            })
        }
    },

    getMyPosts() {
        return (req, res) => {
            Post.getAllPosts(req.user._id, req.user._id, req.query.skip, req.query.limit, req.query.orderBy, req.query.sortOrder, (err, posts) => {

                if (err) {
                    console.log(err.message)
                    return Response.sendError(res, Errors.Internal)
                }

                Response.send(res, posts)
            })
        }
    },

    getPost() {
        return (req, res) => {
            Post.getPost(req.user._id, req.params.postId, (err, posts) => {

                if (err) {
                    console.log(err.message)
                    return Response.sendError(res, Errors.Internal)
                }

                if (posts.length > 0)
                    Response.send(res, posts[0])

            })
        }
    },

    getLikes() {
        return (req, res) => {
            Post.getLikes(req.params.postId, req.query.skip, req.query.limit, (err, likes) => {

                if (err) {
                    console.log(err.message)
                    return Response.sendError(res, Errors.Internal)
                }

                if (!likes)
                    return Response.sendError(res, Errors.NotFound)

                Response.send(res, likes)
            })
        }
    },

    getComments() {
        return (req, res) => {
            Post.getComments(req.params.postId, req.query.skip, req.query.limit, (err, comments) => {

                if (err) {
                    console.log(err.message)
                    return Response.sendError(res, Errors.Internal)
                }

                if (!comments)
                    return Response.sendError(res, Errors.NotFound)

                Response.send(res, comments)
            })
        }
    },

    editPostTitle() {
        return (req, res) => {
            Post.editPostTitle(req.user._id, req.params.postId, req.body.title, (err, post) => {

                if (err) {
                    console.log(err.message)
                    return Response.sendError(res, Errors.Internal)
                }

                if (!post)
                    return Response.sendError(res, Errors.NotFound)

                Response.send(res)
            })
        }
    },

    editPostDescription() {
        return (req, res) => {
            Post.editPostDescription(req.user._id, req.params.postId, req.body.description, (err, post) => {

                if (err) {
                    console.log(err.message)
                    return Response.sendError(res, Errors.Internal)
                }

                if (!post)
                    return Response.sendError(res, Errors.NotFound)

                Response.send(res, post.toObject())
            })
        }
    },

    editComment() {
        return (req, res) => {
            Post.editComment(req.user._id, req.params.postId, req.params.commentId, req.body.comment, (err, post) => {
                if (err) {
                    console.log(err.message)
                    return Response.sendError(res, Errors.Internal)
                }

                if (!post)
                    return Response.sendError(res, Errors.NotFound)

                Response.send(res)
            })
        }
    },

    deletePost() {
        return (req, res) => {
            new Promise((resolve, reject) => {

                Post.deletePost(req.user._id, req.params.postId, (err, post) => {

                    if (err) {
                        console.log(err.message)
                        return reject(Errors.Internal)
                    }

                    if (!post)
                        return reject(Errors.NotFound)


                    resolve(post.url)

                })

            }).then((key) => {

                Utils.deleteObjectsS3(key, (err, data) => {

                    if (err) {
                        console.log(err.message)
                        return reject(Errors.Internal)
                    }

                    Response.send(res)
                    resolve()
                })

            }).catch((errorCode) => {
                Response.sendError(res, errorCode)
            })
        }
    },

    unlikePost() {
        return (req, res) => {
            Post.unlike(req.user._id, req.params.postId, (err, post) => {

                if (err) {
                    console.log(err.message)
                    return Response.sendError(res, Errors.Internal)
                }

                if (!post)
                    return Response.sendError(res, Errors.NotFound)

                Response.send(res)
            })
        }
    },

    deleteComment() {

        return (req, res) => {

            Post.deleteComment(req.user._id, req.params.postId, req.params.commentId, (err, post) => {

                if (err) {
                    console.log(err.message)
                    return Response.sendError(res, Errors.Internal)
                }

                if (!post)
                    return Response.sendError(res, Errors.NotFound)

                Response.send(res)

            })

        }

    }

}

