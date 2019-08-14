import React, { Component } from "react";
import { Link } from "react-router-dom";
import { Text, Card } from "rebass";
import axios from "axios";

class ProjectListItem extends Component {
  state = {
    title: ""
  };

  componentDidMount() {
    const id = this.props.project.id;
    axios
      .get(`http://192.168.1.11:8080/projects/${id}/title`)
      .then(({ data }) => {
        this.setState({ title: data });
      });
  }

  render() {
    const { project } = this.props;
    return (
      <Link to={`/projects/${project.id}`} style={{ textDecoration: "none" }}>
        <Card
          my={2}
          p={3}
          borderColor="line"
          borderStyle="solid"
          border={1}
          borderRadius={8}
        >
          <Text color="text1">
            Id: {project.id} {this.state.title}
          </Text>
        </Card>
      </Link>
    );
  }
}

export default ProjectListItem;
