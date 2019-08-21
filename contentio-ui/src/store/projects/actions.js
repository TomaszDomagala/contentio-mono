import axios from "axios";
import { SET_PROJECT_PAGE } from "./types";
import { apiUrl } from "../../utils/urls";

// export const createProject = (url: string, minDuration: number): CreateProjectAction => ({
//     type: CREATE_PROJECT, url, minDuration
// })

export const createProject = (url, duration) => {
  return dispatch => {
    axios.post(`${apiUrl}/projects/init`, { url, duration });
  };
};

export const getProjectsPage = (page, size = 5) => {
  return async dispatch => {
    try {
      const response = await axios.get(
        "http://192.168.1.11:8080/ui/projects",
        { params: { page, size } }
      );

      dispatch(setProjectPage(response.data));
    } catch (err) {
      console.error(err);
    }
  };
};

export const setProjectPage = page => ({
  type: SET_PROJECT_PAGE,
  page
});
