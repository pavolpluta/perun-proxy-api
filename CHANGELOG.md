# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Example:
```
### Added
- for new features.
### Changed
- for changes in existing functionality.
### Deprecated
- for soon-to-be removed features.
### Removed
- for now removed features.
### Fixed
- for any bug fixes.
### Security
- in case of vulnerabilities.
```

## [Unreleased]
### Added
- added GUI method for listing services
- added method for listing services in JSON format
- added error template
- added GUI method for displaying warning page when accessing service in STAGING or TESTING environment
- added method to get RP environment as JSON
- added method for creating user
### Changed
- the GA4GH external repositories' configuration now supports specifying multiple request headers instead of just a single auth header 

## [v0.2.0]
### Added
- Add POST API methods:
    - /proxy-user/findByExtLogins
    - /proxy-user/findByIdentifiers
    - /proxy-user/findByPerunUserId
    - /proxy-user/{login}
- Add tests for models

### Changed
- Use base64 encoding for  IdPIdentifiers and RPIdentifiers

## [v0.1.1]
### Changed
- Method /proxy-user/findByPerunUserId now accepts passing fields from the request
- Improved logging in Connectors and AttributeObjectMapping

### Fixed
- Fixed bug in method /proxy-user/findByIdentifiers
    - Use exact match in one of the values instead of like
      matching

## [v0.1.0]
### Added
- Add GET API method /proxy-user/findByExtLogins
- Add GET API method /proxy-user/findByIdentifiers
- Add GET API method /proxy-user/findByPerunUserId
- Add GET API method /proxy-user/{login}
- Add GET API method /proxy-user/{login}/entitlements
- Add PUT API method /proxy-user/{login}/identity
- Add GET API method /relying-party/{rp-identifier}/proxy-user/{login}/entitlements

[v0.2.0]: https://github.com/CESNET/perun-proxy-api/commits/tree/v0.2.0
[v0.1.1]: https://github.com/CESNET/perun-proxy-api/commits/tree/v0.1.1
[v0.1.0]: https://github.com/CESNET/perun-proxy-api/commits/tree/v0.1.0
[Unreleased]: https://github.com/CESNET/perun-proxy-api/commits/master