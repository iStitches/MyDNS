import axios from 'axios';

const service = axios.create({
    baseURL: 'http://39.100.119.221:8081',
    timeout: 5000
})

export default service;