import React, { useState } from "react";
import { Box, Text, Button, Card } from "rebass";
import { connect } from "react-redux";
import { createProject } from "../store/projects/actions";

const CreateProject = props => {
  const [url, setUrl] = useState("");
  const [minDuration, setMinDuration] = useState(60);

  const reset = () => {
    setUrl("");
    setMinDuration(60);
  };

  return (
    <Card
      p={3}
      borderColor="divider"
      borderStyle="solid"
      border={1}
      borderRadius={8}
    >
      <Box p={2}>
        <Text fontSize={3} color="text1">Create project</Text>
      </Box>

      <Box p={2}>
        <Text fontSize={1} color="text2">Submission Url</Text>
        <input
          value={url}
          onChange={e => {
            setUrl(e.target.value);
          }}
        />
      </Box>
      <Box p={2}>
        <Text fontSize={1} color="text2">Minimal duration [sec]</Text>
        <input
          value={minDuration}
          onChange={e => {
            setMinDuration(parseInt(e.target.value));
          }}
        />
      </Box>
      <Box p={2}>
        <Button
          bg="primary"
          color="text1"
          onClick={() => {
            props.createProject(url, minDuration);
            reset();
          }}
        >
          Create
        </Button>
      </Box>
    </Card>
  );
};

const mapStateToProps = state => ({});
const mapDispatchToProps = dispatch => ({
  createProject: (url, minDuration) => dispatch(createProject(url, minDuration))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CreateProject);
