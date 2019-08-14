import React, { Component } from "react";
import { connect } from "react-redux";
import axios from "axios";
import { Box, Flex, Card, Text, Heading } from "rebass";
import { fetchDetails, fetchStatement } from "../store/projectdetails/actions";

class SubmissionItem extends Component {
  state = {
    statement: null
  };
  componentDidMount() {
    const { submission } = this.props;
    axios
      .get(`http://192.168.1.11:8080/submissions/${submission.id}/statement`)
      .then(({ data }) => {
        this.setState({ statement: data });
      });
  }

  render() {
    const { submission } = this.props;
    const { statement } = this.state;
    return (
      <Card
        my={3}
        p={3}
        borderStyle="solid"
        border={1}
        borderRadius={8}
        borderColor="line"
      >
        <Flex>
          <Text fontSize={1} color="text2">
            {submission.id}
          </Text>
          <Text mx={2} fontSize={1} fontWeight="bold" color="text2">
            {submission.author}
          </Text>
          <Text color="text2" fontSize={1}>
            {submission.score}
          </Text>
        </Flex>

        {statement ? (
          <Box mt={2}>
            <Text color="text1">{statement.text}</Text>
          </Box>
        ) : null}
      </Card>
    );
  }
}

class ProjectDetails extends Component {
  componentDidMount() {
    const { projectId } = this.props.match.params;
    this.props.fetchDetails(projectId);
    console.log("mount detail");
  }

  render() {
    const { title, submissions, fetchStatement } = this.props;
    return (
      <Box bg="background" style={{ minHeight: "100vh" }}>
        <Box p={3} mx="auto" width={[1, 2 / 3, null, 2 / 5]}>
          <Heading p={1} color="text1">
            {title}
          </Heading>
          {submissions.map(submission => (
            <SubmissionItem
              key={submission.id}
              submission={submission}
              fetchStatement={fetchStatement}
            />
          ))}
        </Box>
      </Box>
    );
  }
}

const mapStateToProps = ({ projectDetailsReducer: details }) => ({
  title: details.title,
  submissions: details.submissions
});
const mapDispatchToProps = dispatch => ({
  fetchDetails: projectId => dispatch(fetchDetails(projectId)),
  fetchStatement: submissionId => dispatch(fetchStatement(submissionId))
});
export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ProjectDetails);
