#!/bin/sh

# Check if pre-commit is installed
if ! command -v pre-commit >/dev/null 2>&1; then
	>&2 echo 'Please install pre-commit to set up git hooks for this repository'
	exit 1
fi

pre-commit install --install-hooks
